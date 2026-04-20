#!/bin/bash

########################################
# CONFIGURACIÓN GENERAL DEL EXPERIMENTO
########################################

# Carpeta raíz donde están tus .class o .jar
PROGRAM_DIR="./Code/Java/Minimalist/v1/Rare"

# Número de ejecuciones por programa
RUNS=100

# Configuración del heap
XMS="512m"
XMX="2g"

# Archivos de salida
DETALLE_CSV="./Data/Times/Java/Minimalist/v1/Rare/resultados_detalle_java.csv"
RESUMEN_CSV="./Data/Times/Java/Minimalist/v1/Rare/resultados_resumen_java.csv"

########################################
# INFO DEL SISTEMA / JVM (CPU, FLAGS)
########################################

# Cores físicos / lógicos
CPU_CORES=$(nproc --all 2>/dev/null || grep -c ^processor /proc/cpuinfo)

# Frecuencia de CPU (intenta max MHz; si no, CPU MHz; si falla, NA)
CPU_MHZ=$(lscpu 2>/dev/null | awk -F: '/CPU max MHz/ {gsub(/ /,"",$2); print $2}' )
if [ -z "$CPU_MHZ" ]; then
  CPU_MHZ=$(lscpu 2>/dev/null | awk -F: '/CPU MHz/ {gsub(/ /,"",$2); print $2; exit}')
fi
if [ -z "$CPU_MHZ" ]; then
  CPU_MHZ="NA"
fi

# Guarda flags de la JVM en un archivo aparte (informativo)
java -XX:+PrintFlagsFinal -version 2> jvm_flags.txt >/dev/null

########################################
# ENCABEZADO DEL CSV DETALLADO
########################################

echo "Programa,Ejecucion,Tiempo_s,Memoria_KB,GC_Invocations,HeapAllocatedBytes,ObjectAllocEvents,CPU_MHz,CPU_Cores,Xms,Xmx" > "$DETALLE_CSV"

########################################
# FUNCIÓN: ejecutar un programa N veces
########################################

run_program () {
  local PROGRAM_CMD="$1"
  local PROGRAM_NAME="$2"

  echo "=== Ejecutando programa: $PROGRAM_NAME ==="

  for ((i=1; i<=RUNS; i++)); do
    echo "  -> Ejecución $i"

    # Archivo JFR por ejecución
    JFR_FILE="recording_${PROGRAM_NAME}_${i}.jfr"

    # Comando final con JFR (settings=profile para incluir allocs/GC)
    # y heap configurado con -Xms / -Xmx
    FULL_CMD="java -Xms$XMS -Xmx$XMX \
      -XX:StartFlightRecording=settings=profile,filename=$JFR_FILE,dumponexit=true \
      $PROGRAM_CMD"

    # Ejecutar midiendo tiempo y memoria
    /usr/bin/time -v bash -c "$FULL_CMD" 1>/dev/null 2>time_output.txt

    ##############################
    # 1) Tiempo y memoria (time)
    ##############################
    RAW_TIME=$(
      grep "Elapsed (wall clock) time" time_output.txt | awk '{print $8}'
    )
    MEM_KB=$(
      grep "Maximum resident set size" time_output.txt | awk '{print $6}'
    )

    # Convertir tiempo a segundos (mm:ss,ms → segundos)
    if [[ "$RAW_TIME" == *m* ]]; then
      MIN=$(echo "$RAW_TIME" | cut -d'm' -f1)
      SEC=$(echo "$RAW_TIME" | cut -d'm' -f2 | sed 's/s//')
      TIME_S=$(echo "$MIN * 60 + $SEC" | bc -l)
    else
      TIME_S=$(echo "$RAW_TIME" | sed 's/s//')
    fi

    ##############################
    # 2) Métricas JFR:
    #    GC, HeapAllocatedBytes, ObjectAllocEvents
    ##############################
    # jfr summary produce tabla:
    # Event Type                      Count  Size (bytes)
    # ...
    # jdk.GarbageCollection             415        12577
    # jdk.ObjectAllocationOutsideTLAB  1013        19220
    # jdk.ObjectAllocationInNewTLAB     359         6719
    #
    # Sumamos:
    #  - GC_Invocations = Count de jdk.GarbageCollection
    #  - ObjectAllocEvents = suma de Count de ObjectAllocation*TLAB
    #  - HeapAllocatedBytes = suma de Size(bytes) de ObjectAllocation*TLAB :contentReference[oaicite:1]{index=1}

    if command -v jfr >/dev/null 2>&1; then
      read GC_INV OBJ_EVENTS HEAP_BYTES <<< "$(
        jfr summary "$JFR_FILE" 2>/dev/null | \
        awk '
          /^ *jdk.GarbageCollection[[:space:]]/ {
            gc=$2
          }
          /^ *jdk.ObjectAllocationOutsideTLAB[[:space:]]/ {
            objCount+=$2; objBytes+=$3
          }
          /^ *jdk.ObjectAllocationInNewTLAB[[:space:]]/ {
            objCount+=$2; objBytes+=$3
          }
          END {
            if (gc      == "") gc      = 0;
            if (objCount== "") objCount= 0;
            if (objBytes== "") objBytes= 0;
            print gc, objCount, objBytes
          }'
      )"
    else
      # Si no existe jfr en PATH, dejamos 0s (puedes ajustar esto)
      GC_INV=0
      OBJ_EVENTS=0
      HEAP_BYTES=0
    fi

    ##############################
    # 3) Escribir fila en CSV
    ##############################
    echo "$PROGRAM_NAME,$i,$TIME_S,$MEM_KB,$GC_INV,$HEAP_BYTES,$OBJ_EVENTS,$CPU_MHZ,$CPU_CORES,$XMS,$XMX" >> "$DETALLE_CSV"

    # Limpieza opcional del archivo JFR por ejecución
    rm -f "$JFR_FILE"
  done

  echo
}

########################################
# DESCUBRIR PROGRAMAS (.jar y .class)
########################################

# .jar → se ejecutan como -jar
for jar in "$PROGRAM_DIR"/*.jar; do
  if [ -f "$jar" ]; then
    PNAME=$(basename "$jar")
    PCMD="-jar \"$jar\""
    run_program "$PCMD" "$PNAME"
  fi
done

# .class → se ejecutan por nombre de clase FQN (maneja paquetes)
# Asumimos que PROGRAM_DIR es la raíz del classpath
while IFS= read -r -d '' classfile; do
  # Ruta relativa respecto a PROGRAM_DIR
  rel="${classfile#$PROGRAM_DIR/}"
  # Quitar extensión .class
  rel_no_ext="${rel%.class}"
  # Reemplazar / por . para armar FQN
  FQN="${rel_no_ext//\//.}"

  PNAME="$FQN"
  # -cp PROGRAM_DIR para que encuentre el paquete raíz
  PCMD="-cp \"$PROGRAM_DIR\" \"$FQN\""

  run_program "$PCMD" "$PNAME"

done < <(find "$PROGRAM_DIR" -type f -name "*.class" -print0)

########################################
# GENERAR RESUMEN (promedio / min / max)
########################################

# Columnas numéricas:
# 3: Tiempo_s
# 4: Memoria_KB
# 5: GC_Invocations
# 6: HeapAllocatedBytes
# 7: ObjectAllocEvents

echo "Programa,NumEjecuciones,Tiempo_prom,Tiempo_min,Tiempo_max,Memoria_prom,Memoria_min,Memoria_max,GC_prom,GC_min,GC_max,HeapBytes_prom,HeapBytes_min,HeapBytes_max,ObjEvents_prom,ObjEvents_min,ObjEvents_max" > "$RESUMEN_CSV"

awk -F',' 'NR>1 {
  prog=$1

  # Inicializar si es primera vez
  if (!(prog in count)) {
    tmin[prog]=$3; tmax[prog]=$3;
    mmin[prog]=$4; mmax[prog]=$4;
    gcmin[prog]=$5; gcmax[prog]=$5;
    hbmin[prog]=$6; hbmax[prog]=$6;
    oemin[prog]=$7; oemax[prog]=$7;
  }

  # Acumular
  count[prog]++
  tsum[prog]+=$3
  msum[prog]+=$4
  gcsum[prog]+=$5
  hbsum[prog]+=$6
  oesum[prog]+=$7

  # Min / Max tiempo
  if ($3 < tmin[prog]) tmin[prog]=$3
  if ($3 > tmax[prog]) tmax[prog]=$3

  # Min / Max memoria
  if ($4 < mmin[prog]) mmin[prog]=$4
  if ($4 > mmax[prog]) mmax[prog]=$4

  # Min / Max GC
  if ($5 < gcmin[prog]) gcmin[prog]=$5
  if ($5 > gcmax[prog]) gcmax[prog]=$5

  # Min / Max HeapBytes
  if ($6 < hbmin[prog]) hbmin[prog]=$6
  if ($6 > hbmax[prog]) hbmax[prog]=$6

  # Min / Max ObjectEvents
  if ($7 < oemin[prog]) oemin[prog]=$7
  if ($7 > oemax[prog]) oemax[prog]=$7

}
END {
  for (p in count) {
    c = count[p]
    tprom = (c ? tsum[p]/c : 0)
    mprom = (c ? msum[p]/c : 0)
    gcprom = (c ? gcsum[p]/c : 0)
    hbprom = (c ? hbsum[p]/c : 0)
    oeprom = (c ? oesum[p]/c : 0)

    printf "%s,%d,%.6f,%.6f,%.6f,%.2f,%.2f,%.2f,%.3f,%.3f,%.3f,%.0f,%.0f,%.0f,%.3f,%.3f,%.3f\n",
           p, c,
           tprom, tmin[p], tmax[p],
           mprom, mmin[p], mmax[p],
           gcprom, gcmin[p], gcmax[p],
           hbprom, hbmin[p], hbmax[p],
           oeprom, oemin[p], oemax[p]
  }
}' "$DETALLE_CSV" >> "$RESUMEN_CSV"

echo "==========================================="
echo " Listo."
echo "  - Detalle por ejecución:   $DETALLE_CSV"
echo "  - Resumen por programa:    $RESUMEN_CSV"
echo "  - Flags JVM capturados en: jvm_flags.txt"
echo "==========================================="