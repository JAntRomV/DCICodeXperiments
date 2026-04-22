#!/usr/bin/env bash

# Carpeta donde están tus fuentes .java (puede contener subcarpetas y paquetes)
SRC_DIR="./Code/Java/Minimalist/v1/Rare"

# Carpeta donde se generarán los .class compilados
CLASS_DIR="./Code/Java/Minimalist/v1/Rare"

# Número de ejecuciones por programa
RUNS=100

# Archivos CSV de salida
RAW_CSV="./Data/Times/Java/Minimalist/v1/Rare/heap_time_memory_raw.csv"
SUMMARY_CSV="./Data/Times/Java/Minimalist/v1/Rare/heap_time_memory_summary.csv"

# Comandos
JAVA_CMD="java"
JFR_CMD="jfr"
TIME_CMD="/usr/bin/time"

# Verificación básica
if ! command -v "$JAVA_CMD" >/dev/null 2>&1; then
  echo "Error: no se encontró el comando 'java' en el PATH."
  exit 1
fi

if ! command -v "$JFR_CMD" >/dev/null 2>&1; then
  echo "Error: no se encontró el comando 'jfr' en el PATH. Asegúrate de usar un JDK (no solo un JRE)."
  exit 1
fi

if [ ! -x "$TIME_CMD" ]; then
  echo "Error: no se encontró '$TIME_CMD'. Instala 'time' (GNU time)."
  exit 1
fi

# Preparar carpeta de clases
# rm -rf "$CLASS_DIR"
# mkdir -p "$CLASS_DIR"

echo "Compilando todos los .java desde $SRC_DIR a $CLASS_DIR ..."
# Compilar TODOS los .java de una vez (mejor para manejar dependencias)
JAVA_FILES=$(find "$SRC_DIR" -name "*.java")
if [ -z "$JAVA_FILES" ]; then
  echo "No se encontraron archivos .java en $SRC_DIR"
  exit 1
fi

javac -d "$CLASS_DIR" $JAVA_FILES || { echo "Error al compilar"; exit 1; }

# CSV crudo: una fila por ejecución
echo "Program,Run,Allocations,TimeSeconds,MaxRSS_KB" > "$RAW_CSV"

#############################################
# Función auxiliar: convertir h:mm:ss a segundos
#############################################
to_seconds() {
  local t="$1"
  # Formatos típicos: h:mm:ss, m:ss, s
  awk -F: '
    NF==3 {print $1*3600 + $2*60 + $3}
    NF==2 {print $1*60 + $2}
    NF==1 {print $1}
  ' <<< "$t"
}

#############################################
# Recorrer cada archivo .java para ejecutar su clase main
#############################################
for file in $JAVA_FILES; do
  [ -e "$file" ] || continue
  FILENAME=$(basename "$file")
  CLASS_NAME="${FILENAME%.java}"

  # Detectar paquete (si existe)
  PACKAGE=$(grep -m1 '^package ' "$file" | sed 's/package\s\+\(.*\);/\1/' )

  if [ -n "$PACKAGE" ]; then
    FQCN="${PACKAGE}.${CLASS_NAME}"
  else
    FQCN="${CLASS_NAME}"
  fi

  echo "==========================================="
  echo "Programa: $FQCN"
  echo "Archivo : $file"
  echo "==========================================="

  for ((i=1; i<=RUNS; i++)); do
    REC_FILE="${CLASS_NAME}_run${i}.jfr"
    TIME_LOG="${CLASS_NAME}_run${i}_time.log"

    echo "  Ejecutando $FQCN (run $i) con JFR y /usr/bin/time ..."

    # Ejecutar con Flight Recorder y medir tiempo/memoria
    # - /usr/bin/time -v escribe en TIME_LOG
    # - JFR graba en REC_FILE
    "$TIME_CMD" -v -o "$TIME_LOG" \
      "$JAVA_CMD" \
        -XX:StartFlightRecording=settings=profile,filename="$REC_FILE",dumponexit=true \
        -cp "$CLASS_DIR" "$FQCN" >/dev/null 2>&1

    if [ ! -f "$REC_FILE" ]; then
      echo "    No se generó $REC_FILE, algo falló en la ejecución de la JVM."
      continue
    fi

    #############################################
    # Extraer Heap Allocations desde el .jfr
    #############################################
    ALLOCS=$("$JFR_CMD" summary "$REC_FILE" 2>/dev/null | \
      awk '
        /jdk.ObjectAllocation/ && $2 ~ /^[0-9]+$/ {
          total += $2
        }
        END {
          if (total == "") total = 0;
          print total
        }')

    #############################################
    # Extraer tiempo y memoria desde /usr/bin/time -v
    #############################################
    TIME_SECS=""
    MAX_RSS=""

    if [ -f "$TIME_LOG" ]; then
      # Elapsed (wall clock) time (h:mm:ss or m:ss)
      ELAPSED_STR=$(grep "Elapsed (wall clock) time" "$TIME_LOG" | awk '{print $8}')
      if [ -n "$ELAPSED_STR" ]; then
        TIME_SECS=$(to_seconds "$ELAPSED_STR")
      fi

      # Maximum resident set size (kbytes)
      MAX_RSS=$(grep "Maximum resident set size (kbytes)" "$TIME_LOG" | awk '{print $6}')
    fi

    echo "    Run $i -> Allocations: $ALLOCS, Time(s): ${TIME_SECS:-NA}, MaxRSS(KB): ${MAX_RSS:-NA}"

    # Guardar fila en CSV crudo
    echo "${FQCN},${i},${ALLOCS},${TIME_SECS},${MAX_RSS}" >> "$RAW_CSV"

    # Limpiar archivos temporales (opcional)
    rm -f "$REC_FILE" "$TIME_LOG"
  done
done

#############################################
# Generar resumen por programa
#############################################
echo "Generando resumen en $SUMMARY_CSV ..."

awk -F',' '
  NR==1 { next }  # saltar cabecera
  {
    prog = $1
    alloc = $3 + 0
    t = $4 + 0
    mem = $5 + 0

    sumAlloc[prog] += alloc
    sumTime[prog]  += t
    sumMem[prog]   += mem
    count[prog]++

    if (!(prog in minAlloc) || alloc < minAlloc[prog]) minAlloc[prog] = alloc
    if (!(prog in maxAlloc) || alloc > maxAlloc[prog]) maxAlloc[prog] = alloc

    if (!(prog in minTime) || t < minTime[prog]) minTime[prog] = t
    if (!(prog in maxTime) || t > maxTime[prog]) maxTime[prog] = t

    if (!(prog in minMem) || mem < minMem[prog]) minMem[prog] = mem
    if (!(prog in maxMem) || mem > maxMem[prog]) maxMem[prog] = mem
  }
  END {
    print "Program,NumRuns,AvgAllocations,MinAllocations,MaxAllocations,AvgTimeSeconds,MinTimeSeconds,MaxTimeSeconds,AvgMemoryKB,MinMemoryKB,MaxMemoryKB"
    for (p in sumAlloc) {
      n = count[p]
      avgAlloc = (n > 0 ? sumAlloc[p] / n : 0)
      avgTime  = (n > 0 ? sumTime[p]  / n : 0)
      avgMem   = (n > 0 ? sumMem[p]   / n : 0)
      printf "%s,%d,%.2f,%d,%d,%.4f,%.4f,%.4f,%.2f,%d,%d\n",
             p, n,
             avgAlloc, minAlloc[p], maxAlloc[p],
             avgTime,  minTime[p],  maxTime[p],
             avgMem,   minMem[p],   maxMem[p]
    }
  }
' "$RAW_CSV" > "$SUMMARY_CSV"

echo "Listo."
echo "  Detalle por ejecución: $RAW_CSV"
echo "  Resumen por programa : $SUMMARY_CSV"