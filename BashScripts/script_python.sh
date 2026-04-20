#!/usr/bin/env bash

# ======================================================
# bench_python_multi.sh
#
# Ejecuta TODOS los scripts .py de una carpeta N veces y:
#   - Guarda en un CSV el tiempo y memoria de CADA ejecución
#   - Genera un CSV de resumen por script con promedios, mínimos y máximos
#
# Uso:
#   ./bench_python_multi.sh carpeta_scripts [num_ejecuciones] [csv_detalle] [csv_resumen]
#
# Ejemplo:
#   ./bench_python_multi.sh ./scripts 100 detalle.csv resumen.csv
# ======================================================

# Carpeta con scripts .py
SCRIPTS_DIR="$1"

if [ -z "$SCRIPTS_DIR" ]; then
  echo "Uso: $0 carpeta_scripts [num_ejecuciones] [csv_detalle] [csv_resumen]"
  exit 1
fi

if [ ! -d "$SCRIPTS_DIR" ]; then
  echo "Error: no se encontró la carpeta '$SCRIPTS_DIR'"
  exit 1
fi

# Número de ejecuciones por script (por defecto 100)
RUNS="${2:-100}"

# Archivos CSV de salida
DETAIL_CSV="./Data/Times/Python/Human/v1/Rare/${3:-resultados_detalle_python.csv}"
SUMMARY_CSV="./Data/Times/Python/Human/v1/Rare/${4:-resultados_resumen_python.csv}"

# Comando de Python (ajusta si usas 'python' u otro)
PYTHON_CMD="python3"

# Comando time (GNU time)
TIME_CMD="/usr/bin/time"

if ! command -v "$TIME_CMD" >/dev/null 2>&1; then
  echo "Error: no se encontró GNU time en '$TIME_CMD'."
  echo "Instálalo (p.ej. en Ubuntu: sudo apt-get install time) o ajusta la ruta en el script."
  exit 1
fi

# Archivo temporal para capturar la salida de 'time'
TMPFILE="$(mktemp)"

# Preparar CSVs
echo "script,run,time_s,mem_kb" > "$DETAIL_CSV"
echo "script,runs,avg_time_s,min_time_s,max_time_s,avg_mem_kb,min_mem_kb,max_mem_kb" > "$SUMMARY_CSV"

echo "Buscando scripts .py en '$SCRIPTS_DIR'..."
echo

shopt -s nullglob
PY_SCRIPTS=("$SCRIPTS_DIR"/*.py)

if [ ${#PY_SCRIPTS[@]} -eq 0 ]; then
  echo "No se encontraron archivos .py en '$SCRIPTS_DIR'."
  rm -f "$TMPFILE"
  exit 1
fi

for PY_SCRIPT in "${PY_SCRIPTS[@]}"; do
  if [ ! -f "$PY_SCRIPT" ]; then
    continue
  fi

  script_name="$(basename "$PY_SCRIPT")"
  echo "==========================================="
  echo "Script: $script_name"
  echo "Ejecuciones: $RUNS"
  echo "==========================================="

  # Acumuladores por script
  sum_time=0
  sum_mem=0
  min_time=
  max_time=
  min_mem=
  max_mem=

  for ((i=1; i<=RUNS; i++)); do
    # Ejecuta el script y mide tiempo y memoria
    # %e = tiempo real (wall-clock) en segundos
    # %M = máximo uso de memoria residente (KB)
    "$TIME_CMD" -f "%e %M" -o "$TMPFILE" \
      "$PYTHON_CMD" "$PY_SCRIPT" >/dev/null 2>&1

    # Lee tiempo y memoria
    read current_time current_mem < "$TMPFILE"

    echo "  -> Ejecución $i: tiempo = $current_time s, memoria = ${current_mem} KB"

    # Guarda detalle en CSV
    echo "$script_name,$i,$current_time,$current_mem" >> "$DETAIL_CSV"

    # Acumula (float con awk)
    sum_time=$(awk -v s="$sum_time" -v x="$current_time" 'BEGIN {print s + x}')
    sum_mem=$(awk -v s="$sum_mem" -v x="$current_mem" 'BEGIN {print s + x}')

    # Inicializa min/max en la primera iteración
    if [ -z "$min_time" ]; then
      min_time="$current_time"
      max_time="$current_time"
      min_mem="$current_mem"
      max_mem="$current_mem"
    else
      # Actualiza min/max de tiempo
      min_time=$(awk -v a="$min_time" -v b="$current_time" 'BEGIN {if (b < a) print b; else print a}')
      max_time=$(awk -v a="$max_time" -v b="$current_time" 'BEGIN {if (b > a) print b; else print a}')
      # Actualiza min/max de memoria
      min_mem=$(awk -v a="$min_mem" -v b="$current_mem" 'BEGIN {if (b < a) print b; else print a}')
      max_mem=$(awk -v a="$max_mem" -v b="$current_mem" 'BEGIN {if (b > a) print b; else print a}')
    fi
  done

  # Calcula promedios por script
  avg_time=$(awk -v s="$sum_time" -v n="$RUNS" 'BEGIN {print s / n}')
  avg_mem=$(awk -v s="$sum_mem" -v n="$RUNS" 'BEGIN {print s / n}')

  echo "  RESUMEN para $script_name"
  echo "    Tiempo (s):  avg = $avg_time, min = $min_time, max = $max_time"
  echo "    Memoria KB:  avg = $avg_mem, min = $min_mem, max = $max_mem"
  echo

  # Escribe resumen en CSV de resumen
  echo "$script_name,$RUNS,$avg_time,$min_time,$max_time,$avg_mem,$min_mem,$max_mem" >> "$SUMMARY_CSV"

done

# Limpia archivo temporal
rm -f "$TMPFILE"

echo "==========================================="
echo "Proceso terminado."
echo "Detalle de ejecuciones en:   $DETAIL_CSV"
echo "Resumen por script en:       $SUMMARY_CSV"
echo "==========================================="