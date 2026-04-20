#!/usr/bin/env bash
set -euo pipefail

########################################
# CONFIGURACIÓN
########################################

# Carpeta donde están tus .java
SRC_DIR="./Code/Java/Minimalist/v1/Rare"           # cámbiala si es necesario

# Carpeta donde se generarán los .class
BUILD_DIR="./Code/Java/Minimalist/v1/Rare"

# Nombre del paquete (como lo declaras en los .java)
PACKAGE_NAME="Code.Java.Minimalist.v1.Rare"

# Número de ejecuciones por programa
RUNS=100

# Archivos de salida
RAW_CSV="./Data/Times/Java/Minimalist/v1/Rare/java_metrics_raw.csv"
SUMMARY_CSV="./Data/Times/Java/Minimalist/v1/Rare/java_metrics_summary.csv"

# Comando time (versión GNU, con -v)
TIME_CMD="/usr/bin/time"

########################################
# VALIDACIONES BÁSICAS
########################################

if ! command -v javac >/dev/null 2>&1; then
  echo "Error: javac no encontrado en PATH."
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  echo "Error: java no encontrado en PATH."
  exit 1
fi

if [ ! -x "$TIME_CMD" ]; then
  echo "Error: no se encontró /usr/bin/time (GNU time)."
  echo "Instálalo (por ejemplo: sudo apt-get install time) o ajusta TIME_CMD."
  exit 1
fi

if ! command -v jfr >/dev/null 2>&1; then
  echo "Error: la herramienta 'jfr' no está en el PATH."
  echo "Asegúrate de usar el JDK 25 (no solo el JRE) y de que \$JAVA_HOME/bin esté en el PATH."
  exit 1
fi

########################################
# COMPILACIÓN
########################################

# echo "Compilando fuentes Java desde: $SRC_DIR ..."
mkdir -p "$BUILD_DIR"

javac -d "$BUILD_DIR" "$SRC_DIR"/*.java

########################################
# PREPARAR ARCHIVOS CSV
########################################

# CSV crudo: programa, ejecución, GC, user_time, elapsed_time, max_rss, heap_bytes, alloc_events
echo "Program,Run,GCInvocations,UserTime_s,ElapsedTime_s,MaxRSS_KB,HeapAllocatedBytes,ObjectAllocEvents" > "$RAW_CSV"

########################################
# FUNCIÓN PARA OBTENER CLASES CON main
########################################

get_main_classes() {
  for src in "$SRC_DIR"/*.java; do
    if grep -q "public static void main" "$src"; then
      basename="$(basename "$src" .java)"
      echo "$basename"
    fi
  done
}

########################################
# FUNCIÓN PARA CONVERTIR h:mm:ss o m:ss A SEGUNDOS (float)
########################################

to_seconds() {
  local t="$1"
  awk -v t="$t" 'BEGIN {
    n = split(t, a, ":")
    if (n == 3) {
      h = a[1]; m = a[2]; s = a[3]
    } else if (n == 2) {
      h = 0; m = a[1]; s = a[2]
    } else {
      h = 0; m = 0; s = a[1]
    }
    split(s, b, ".")
    sec = b[1]
    frac = (length(b) > 1 ? ("0." b[2]) : 0)
    total = h*3600 + m*60 + sec + frac
    print total
  }'
}

########################################
# EJECUCIÓN DE PROGRAMAS
########################################

echo "Buscando clases con método main..."

MAIN_CLASSES=()
while IFS= read -r cls; do
  MAIN_CLASSES+=("$cls")
done < <(get_main_classes)

if [ "${#MAIN_CLASSES[@]}" -eq 0 ]; then
  echo "No se encontraron clases con 'public static void main' en $SRC_DIR"
  exit 1
fi

echo "Clases con main encontradas:"
printf ' - %s\n' "${MAIN_CLASSES[@]}"

for class_name in "${MAIN_CLASSES[@]}"; do
  FQCN="${PACKAGE_NAME}.${class_name}"
  echo "Ejecutando programa: $FQCN ($RUNS veces)..."

  for ((i=1; i<=RUNS; i++)); do
    GC_LOG="$(mktemp)"
    TIME_LOG="$(mktemp)"
    JFR_FILE="$(mktemp --suffix=.jfr)"

    # Ejecutar con:
    # - JFR: StartFlightRecording (settings=profile para incluir allocations)
    # - Log de GC
    # - /usr/bin/time -v para tiempo y memoria
    $TIME_CMD -v java \
      -XX:StartFlightRecording=name=run_${class_name}_${i},settings=profile,filename="$JFR_FILE",dumponexit=true \
      -Xlog:gc*:file="$GC_LOG" \
      -cp "$BUILD_DIR" "$FQCN" \
      >/dev/null 2> "$TIME_LOG" || true

    # Contar invocaciones de GC
    GC_COUNT=$(grep -c "\[gc" "$GC_LOG" 2>/dev/null || echo 0)

    # Parsear /usr/bin/time -v
    USER_TIME=$(
      grep "User time (seconds)" "$TIME_LOG" | \
      awk -F': ' '{print $2}' | xargs
    )

    ELAPSED_STR=$(
      grep "Elapsed (wall clock) time" "$TIME_LOG" | \
      awk -F': ' '{print $2}' | xargs
    )

    MAX_RSS=$(
      grep "Maximum resident set size (kbytes)" "$TIME_LOG" | \
      awk -F': ' '{print $2}' | xargs
    )

    ELAPSED_SEC=$(to_seconds "$ELAPSED_STR")

    # Parsear JFR para obtener HeapAllocatedBytes y ObjectAllocEvents
    # Sumamos allocationSize de ambas:
    #   jdk.ObjectAllocationInNewTLAB
    #   jdk.ObjectAllocationOutsideTLAB
    read HEAP_ALLOC_BYTES HEAP_ALLOC_EVENTS < <(
      jfr print --events jdk.ObjectAllocationInNewTLAB,jdk.ObjectAllocationOutsideTLAB "$JFR_FILE" 2>/dev/null | \
      awk '
        /allocationSize =/ {
          # El tamaño suele estar en la tercera columna, ej: allocationSize = 1234
          for (i=1; i<=NF; i++) {
            if ($i == "allocationSize") {
              # siguiente token debe ser "=" y luego el valor
              if ((i+2) <= NF) {
                val=$(i+2)
                gsub(",", "", val)
                bytes += val
                events++
                break
              }
            }
          }
        }
        END {
          if (bytes == "" || bytes < 0) bytes = 0
          if (events == "" || events < 0) events = 0
          print bytes, events
        }'
    )

    # Si algo salió raro, asegurar números
    HEAP_ALLOC_BYTES=${HEAP_ALLOC_BYTES:-0}
    HEAP_ALLOC_EVENTS=${HEAP_ALLOC_EVENTS:-0}

    # Guardar en CSV crudo
    echo "${class_name},${i},${GC_COUNT},${USER_TIME},${ELAPSED_SEC},${MAX_RSS},${HEAP_ALLOC_BYTES},${HEAP_ALLOC_EVENTS}" >> "$RAW_CSV"

    # Limpiar temporales
    rm -f "$GC_LOG" "$TIME_LOG" "$JFR_FILE"

    # Feedback simple en consola
    printf "\r  -> %s run %3d/%3d: GC=%d, User=%.4fs, Elapsed=%.4fs, MaxRSS=%s KB, HeapBytes=%s, AllocEvents=%s" \
      "$class_name" "$i" "$RUNS" "$GC_COUNT" "$USER_TIME" "$ELAPSED_SEC" "$MAX_RSS" "$HEAP_ALLOC_BYTES" "$HEAP_ALLOC_EVENTS"
  done
  echo    # salto de línea después del programa
done

########################################
# RESUMEN (PROMEDIO, MÍN, MÁX POR PROGRAMA)
########################################

echo "Generando resumen en: $SUMMARY_CSV ..."

awk -F',' '
NR==1 { next }  # saltar encabezado
{
  prog = $1
  gc   = $3+0
  ut   = $4+0
  et   = $5+0
  rss  = $6+0
  hb   = $7+0
  ev   = $8+0

  n[prog]++

  sum_gc[prog]  += gc
  sum_ut[prog]  += ut
  sum_et[prog]  += et
  sum_rss[prog] += rss
  sum_hb[prog]  += hb
  sum_ev[prog]  += ev

  if (!(prog in min_gc)  || gc  < min_gc[prog])  min_gc[prog]  = gc
  if (!(prog in max_gc)  || gc  > max_gc[prog])  max_gc[prog]  = gc

  if (!(prog in min_ut)  || ut  < min_ut[prog])  min_ut[prog]  = ut
  if (!(prog in max_ut)  || ut  > max_ut[prog])  max_ut[prog]  = ut

  if (!(prog in min_et)  || et  < min_et[prog])  min_et[prog]  = et
  if (!(prog in max_et)  || et  > max_et[prog])  max_et[prog]  = et

  if (!(prog in min_rss) || rss < min_rss[prog]) min_rss[prog] = rss
  if (!(prog in max_rss) || rss > max_rss[prog]) max_rss[prog] = rss

  if (!(prog in min_hb)  || hb  < min_hb[prog]) min_hb[prog]  = hb
  if (!(prog in max_hb)  || hb  > max_hb[prog]) max_hb[prog]  = hb

  if (!(prog in min_ev)  || ev  < min_ev[prog]) min_ev[prog]  = ev
  if (!(prog in max_ev)  || ev  > max_ev[prog]) max_ev[prog]  = ev
}
END {
  print "Program," \
        "AvgGCInvocations,MinGCInvocations,MaxGCInvocations," \
        "AvgUserTime_s,MinUserTime_s,MaxUserTime_s," \
        "AvgElapsedTime_s,MinElapsedTime_s,MaxElapsedTime_s," \
        "AvgMaxRSS_KB,MinMaxRSS_KB,MaxMaxRSS_KB," \
        "AvgHeapAllocatedBytes,MinHeapAllocatedBytes,MaxHeapAllocatedBytes," \
        "AvgObjectAllocEvents,MinObjectAllocEvents,MaxObjectAllocEvents"

  for (p in n) {
    avg_gc  = sum_gc[p]  / n[p]
    avg_ut  = sum_ut[p]  / n[p]
    avg_et  = sum_et[p]  / n[p]
    avg_rss = sum_rss[p] / n[p]
    avg_hb  = sum_hb[p]  / n[p]
    avg_ev  = sum_ev[p]  / n[p]

    printf "%s,%.4f,%d,%d,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.2f,%.2f,%.2f,%.0f,%.0f,%.0f,%.2f,%.2f,%.2f\n", \
           p, avg_gc, min_gc[p], max_gc[p], \
           avg_ut, min_ut[p], max_ut[p], \
           avg_et, min_et[p], max_et[p], \
           avg_rss, min_rss[p], max_rss[p], \
           avg_hb, min_hb[p], max_hb[p], \
           avg_ev, min_ev[p], max_ev[p]
  }
}
' "$RAW_CSV" > "$SUMMARY_CSV"

echo "Listo."
echo "Resultados por ejecución:   $RAW_CSV"
echo "Resumen por programa:       $SUMMARY_CSV"