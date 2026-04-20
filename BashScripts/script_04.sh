#!/usr/bin/env bash
set -euo pipefail

# Carpeta donde están las clases compiladas (.class)
CLASSES_DIR="./Code/Minimalist/v1/Rare"   # <-- cámbiala si tus .class están en otra carpeta

# Número de ejecuciones por clase
ITERACIONES=100

# Archivos de salida
OUTPUT_DETALLE="./Data/Times/Java/Minimalist/v1/Rare/resultados_detalle.csv"
OUTPUT_RESUMEN="./Data/Times/Java/Minimalist/v1/Rare/resultados_resumen.csv"

# Validar que exista /usr/bin/time
if ! command -v /usr/bin/time >/dev/null 2>&1; then
  echo "Error: /usr/bin/time no está disponible. Instálalo o ajusta la ruta en el script."
  exit 1
fi

# Validar carpeta de clases
if [ ! -d "$CLASSES_DIR" ]; then
  echo "Error: la carpeta '$CLASSES_DIR' no existe."
  exit 1
fi

# Buscar archivos .class
shopt -s nullglob
class_files=("$CLASSES_DIR"/*.class)
shopt -u nullglob

if [ ${#class_files[@]} -eq 0 ]; then
  echo "No se encontraron archivos .class en $CLASSES_DIR"
  exit 1
fi

# CSV de detalle
echo "class,run,time_seconds,max_rss_kb" > "$OUTPUT_DETALLE"

# Archivo temporal para /usr/bin/time
TMPFILE=$(mktemp)

for classfile in "${class_files[@]}"; do
  classname=$(basename "$classfile" .class)

  echo "Midiendo clase: $classname"

  for ((i=1; i<=ITERACIONES; i++)); do
    # Ejecutar la clase y medir tiempo y memoria
    # %e = tiempo real en segundos
    # %M = máximo RSS en KB
    /usr/bin/time -f "%e,%M" -o "$TMPFILE" \
      java "Code.Minimalist.v1.Rare.$classname" >/dev/null

    IFS=, read -r tiempo memoria < "$TMPFILE"

    echo "$classname,$i,$tiempo,$memoria" >> "$OUTPUT_DETALLE"
  done
done

rm -f "$TMPFILE"

# Generar resumen por clase
echo "class,avg_time_s,min_time_s,max_time_s,avg_mem_kb,min_mem_kb,max_mem_kb" > "$OUTPUT_RESUMEN"

awk -F, 'NR > 1 {
  clase = $1
  tiempo = $3 + 0
  mem = $4 + 0

  count[clase]++
  sumT[clase] += tiempo
  sumM[clase] += mem

  if (!(clase in minT) || tiempo < minT[clase]) minT[clase] = tiempo
  if (!(clase in maxT) || tiempo > maxT[clase]) maxT[clase] = tiempo

  if (!(clase in minM) || mem < minM[clase]) minM[clase] = mem
  if (!(clase in maxM) || mem > maxM[clase]) maxM[clase] = mem
}
END {
  for (c in count) {
    avgT = sumT[c] / count[c]
    avgM = sumM[c] / count[c]
    printf "%s,%.6f,%.6f,%.6f,%.2f,%.0f,%.0f\n", \
      c, avgT, minT[c], maxT[c], avgM, minM[c], maxM[c]
  }
}' "$OUTPUT_DETALLE" >> "$OUTPUT_RESUMEN"

echo "Listo ✅"
echo "  Detalle de ejecuciones: $OUTPUT_DETALLE"
echo "  Resumen de métricas:    $OUTPUT_RESUMEN"