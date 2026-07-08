#!/usr/bin/env bash
#===============================================================================
# benchmark_runner.sh
#
# Ejecuta N veces (por defecto 100) un conjunto de clases Java (con main())
# y scripts Python, midiendo:
#   - tiempo de ejecucion end-to-end en NANOSEGUNDOS (reloj monotonico)
#   - memoria residente maxima (Peak RSS) en KB
#
# Modelo formal:
#   T = { T_java U T_py }                         (conjunto de objetivos)
#   R(t) = {r_1,...,r_n}, n = ITERATIONS           (repeticiones por objetivo)
#   M(r_i) = (tau_i, mu_i, exit_i)                 (tiempo, memoria, codigo salida)
#
# Salidas:
#   $OUTPUT_DIR/raw_measurements.csv     -> una fila por ejecucion individual
#   $OUTPUT_DIR/summary_statistics.csv   -> mean, std, var, min, max por objetivo
#
# Requisitos: bash >= 4, java, javap, python3, /usr/bin/time (GNU en Linux,
# BSD en macOS), awk, date con soporte %N (GNU coreutils).
#===============================================================================

set -uo pipefail

# ---------------------------------------------------------------------------
# 1. CONFIGURACION (sobreescribible via variables de entorno o flags CLI)
# ---------------------------------------------------------------------------
JAVA_DIR="${JAVA_DIR:-./targets/java}"
JAVA_CLASSPATH="${JAVA_CLASSPATH:-$JAVA_DIR}"
PYTHON_DIR="${PYTHON_DIR:-./targets/python}"
ITERATIONS="${ITERATIONS:-100}"
OUTPUT_DIR="${OUTPUT_DIR:-./benchmark_results}"
PYTHON_BIN="${PYTHON_BIN:-python3}"
DEBUG="${DEBUG:-0}"

usage() {
  cat <<EOF
Uso: $0 [opciones]

Opciones:
  --java-dir DIR        Directorio raiz de clases .class compiladas (default: $JAVA_DIR)
  --classpath CP        Classpath para 'java'/'javap' (default: = java-dir)
  --python-dir DIR      Directorio con scripts .py (default: $PYTHON_DIR)
  --iterations N        Numero de repeticiones por objetivo (default: $ITERATIONS)
  --output-dir DIR      Carpeta de salida para CSVs (default: $OUTPUT_DIR)
  --debug               Traza por candidato: por que se acepto/rechazo cada clase
  -h, --help            Muestra esta ayuda

Variables de entorno equivalentes: JAVA_DIR, JAVA_CLASSPATH, PYTHON_DIR,
ITERATIONS, OUTPUT_DIR, PYTHON_BIN, DEBUG=1.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --java-dir) JAVA_DIR="$2"; shift 2 ;;
    --classpath) JAVA_CLASSPATH="$2"; shift 2 ;;
    --python-dir) PYTHON_DIR="$2"; shift 2 ;;
    --iterations) ITERATIONS="$2"; shift 2 ;;
    --output-dir) OUTPUT_DIR="$2"; shift 2 ;;
    --debug) DEBUG=1; shift 1 ;;
    -h|--help) usage; exit 0 ;;
    *) echo "Opcion desconocida: $1" >&2; usage; exit 1 ;;
  esac
done

# Normalizacion critica: elimina trailing slash. Sin esto, "targets/java/"
# rompe el prefix-stripping "${class_file#"$JAVA_DIR"/}" en
# discover_java_targets (busca "targets/java//" y no coincide con
# "targets/java/com/example/Foo.class"), dejando C = vacio silenciosamente.
JAVA_DIR="${JAVA_DIR%/}"
JAVA_CLASSPATH="${JAVA_CLASSPATH%/}"
PYTHON_DIR="${PYTHON_DIR%/}"

RAW_CSV="$OUTPUT_DIR/raw_measurements.csv"
SUMMARY_CSV="$OUTPUT_DIR/summary_statistics.csv"

mkdir -p "$OUTPUT_DIR"

timestamp_utc() { date -u +"%Y-%m-%dT%H:%M:%S.%3NZ" 2>/dev/null || date -u +"%Y-%m-%dT%H:%M:%SZ"; }

# ---------------------------------------------------------------------------
# 2. DETECCION DE SO Y DEPENDENCIAS
# ---------------------------------------------------------------------------
OS_TYPE="$(uname -s)"

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || { echo "ERROR: comando '$1' no encontrado en PATH." >&2; exit 1; }
}
require_cmd java
require_cmd javap
require_cmd "$PYTHON_BIN"
require_cmd awk
require_cmd date

TIME_BIN="/usr/bin/time"
if [[ ! -x "$TIME_BIN" ]]; then
  echo "ERROR: se requiere /usr/bin/time." >&2
  if [[ "$OS_TYPE" == "Darwin" ]]; then
    echo "En macOS deberia existir de forma nativa; si no, revisa tu PATH." >&2
  else
    echo "En Debian/Ubuntu: sudo apt-get install time" >&2
  fi
  exit 1
fi

if [[ ! -d "$JAVA_DIR" ]] && [[ ! -d "$PYTHON_DIR" ]]; then
  echo "ERROR: ni JAVA_DIR ($JAVA_DIR) ni PYTHON_DIR ($PYTHON_DIR) existen." >&2
  exit 1
fi

# ---------------------------------------------------------------------------
# 3. PARSEO DE MEMORIA (portable Linux/macOS)
#    Linux (GNU time -v): "Maximum resident set size (kbytes): N"
#    macOS (BSD time -l): "       N  maximum resident set size"  (bytes)
# ---------------------------------------------------------------------------
parse_memory_kb() {
  local time_output_file="$1"
  local kb=""
  if [[ "$OS_TYPE" == "Darwin" ]]; then
    kb=$(grep "maximum resident set size" "$time_output_file" 2>/dev/null | awk '{print $1}')
    if [[ -n "$kb" ]]; then
      kb=$(( kb / 1024 ))
    fi
  else
    kb=$(grep "Maximum resident set size" "$time_output_file" 2>/dev/null | awk -F': ' '{print $2}' | tr -d ' ')
  fi
  echo "${kb:-0}"
}

# ---------------------------------------------------------------------------
# 4. NUCLEO DE MEDICION
#    measure_once <cmd...> -> imprime "elapsed_ns,mem_kb,exit_code"
#
#    tau  = reloj monotonico (date +%s%N) alrededor del proceso completo
#    mu   = Peak RSS reportado por /usr/bin/time
#
#    NOTA: tau incluye el overhead de fork/exec del propio wrapper 'time'
#    (delta_exec) ademas del arranque de la JVM/interprete (delta_startup),
#    consistente con el modelo T_hat = T + delta + epsilon.
# ---------------------------------------------------------------------------
measure_once() {
  local time_log elapsed_ns mem_kb exit_code start_ns end_ns
  time_log=$(mktemp)

  if [[ "$OS_TYPE" == "Darwin" ]]; then
    start_ns=$(date +%s%N)
    "$TIME_BIN" -l -- "$@" > /dev/null 2> "$time_log"
    exit_code=$?
    end_ns=$(date +%s%N)
  else
    start_ns=$(date +%s%N)
    "$TIME_BIN" -v -o "$time_log" -- "$@" > /dev/null 2> /dev/null
    exit_code=$?
    end_ns=$(date +%s%N)
  fi

  elapsed_ns=$(( end_ns - start_ns ))
  mem_kb=$(parse_memory_kb "$time_log")
  rm -f "$time_log"

  printf '%s,%s,%s\n' "$elapsed_ns" "$mem_kb" "$exit_code"
}

# ---------------------------------------------------------------------------
# 5. DESCUBRIMIENTO DE OBJETIVOS
#    Java: cada .class bajo JAVA_DIR -> FQCN, filtrando clases internas/
#    anonimas ('$') y validando presencia de main(String[]) via javap.
# ---------------------------------------------------------------------------
discover_java_targets() {
  if [[ ! -d "$JAVA_DIR" ]]; then
    [[ "$DEBUG" == "1" ]] && echo "[DEBUG] JAVA_DIR no existe: $JAVA_DIR" >&2
    return 0
  fi

  local class_count=0
  local class_file rel fqcn has_main javap_err

  while IFS= read -r -d '' class_file; do
    class_count=$((class_count + 1))
    rel="${class_file#"$JAVA_DIR"/}"
    rel="${rel%.class}"
    fqcn="${rel//\//.}"

    if [[ "$fqcn" == *'$'* ]]; then
      [[ "$DEBUG" == "1" ]] && echo "[DEBUG] SKIP (clase interna/anonima): $fqcn" >&2
      continue
    fi

    javap_err=$(javap -public -classpath "$JAVA_CLASSPATH" "$fqcn" 2>&1)
    # Acepta tanto 'String[])' como 'String...)' (main declarado con varargs)
    has_main=$(grep -Ec 'public static void main\(java\.lang\.String(\[\]|\.\.\.)\)' <<< "$javap_err")

    if [[ "$has_main" -gt 0 ]]; then
      [[ "$DEBUG" == "1" ]] && echo "[DEBUG] OK: $fqcn" >&2
      echo "$fqcn"
    else
      [[ "$DEBUG" == "1" ]] && echo "[DEBUG] SIN main() o javap fallo -> $fqcn :: $(head -1 <<< "$javap_err")" >&2
    fi
  done < <(find "$JAVA_DIR" -name "*.class" -print0)

  if [[ "$DEBUG" == "1" ]]; then
    echo "[DEBUG] Total .class encontrados bajo JAVA_DIR: $class_count" >&2
    if [[ "$class_count" -eq 0 ]]; then
      echo "[DEBUG] -> Caso A: revisa JAVA_DIR='$JAVA_DIR' (existen .java sin compilar? ruta relativa desde otro cwd? trailing slash ya normalizado por el script)" >&2
    fi
  fi
}

discover_python_targets() {
  [[ -d "$PYTHON_DIR" ]] || return 0
  find "$PYTHON_DIR" -name "*.py" -print
}

# ---------------------------------------------------------------------------
# 6. BUCLE PRINCIPAL DE EJECUCION
# ---------------------------------------------------------------------------
echo "run_id,target_type,target_name,iteration,timestamp_utc,exec_time_ns,memory_kb,exit_code" > "$RAW_CSV"

run_id=0

echo "[1/3] Descubriendo clases Java con main()..."
mapfile -t JAVA_TARGETS < <(discover_java_targets)
echo "      -> ${#JAVA_TARGETS[@]} clase(s) encontrada(s)."

echo "[2/3] Descubriendo scripts Python..."
mapfile -t PYTHON_TARGETS < <(discover_python_targets)
echo "      -> ${#PYTHON_TARGETS[@]} script(s) encontrado(s)."

if [[ ${#JAVA_TARGETS[@]} -eq 0 && ${#PYTHON_TARGETS[@]} -eq 0 ]]; then
  echo "ERROR: no se encontraron objetivos ejecutables." >&2
  exit 1
fi

echo "[3/3] Ejecutando benchmarks ($ITERATIONS iteraciones por objetivo)..."

for fqcn in "${JAVA_TARGETS[@]}"; do
  echo "  >> Java: $fqcn"
  for ((i = 1; i <= ITERATIONS; i++)); do
    result=$(measure_once java -cp "$JAVA_CLASSPATH" "$fqcn")
    IFS=',' read -r elapsed_ns mem_kb exit_code <<< "$result"
    run_id=$((run_id + 1))
    echo "$run_id,java,$fqcn,$i,$(timestamp_utc),$elapsed_ns,$mem_kb,$exit_code" >> "$RAW_CSV"
    if (( i % 20 == 0 )); then echo "     ...$i/$ITERATIONS"; fi
  done
done

for script in "${PYTHON_TARGETS[@]}"; do
  name=$(basename "$script")
  echo "  >> Python: $name"
  for ((i = 1; i <= ITERATIONS; i++)); do
    result=$(measure_once "$PYTHON_BIN" "$script")
    IFS=',' read -r elapsed_ns mem_kb exit_code <<< "$result"
    run_id=$((run_id + 1))
    echo "$run_id,python,$name,$i,$(timestamp_utc),$elapsed_ns,$mem_kb,$exit_code" >> "$RAW_CSV"
    if (( i % 20 == 0 )); then echo "     ...$i/$ITERATIONS"; fi
  done
done

echo "Mediciones crudas guardadas en: $RAW_CSV"

# ---------------------------------------------------------------------------
# 7. RESUMEN ESTADISTICO
#    mean, varianza MUESTRAL (n-1, consistente con pandas .std(ddof=1)),
#    std, min, max -- calculado por (target_type, target_name) via awk,
#    con formula de un solo paso: var = (sum(x^2) - n*mean^2) / (n-1)
# ---------------------------------------------------------------------------
compute_summary() {
  {
    echo "target_type,target_name,n,time_ns_mean,time_ns_std,time_ns_var,time_ns_min,time_ns_max,mem_kb_mean,mem_kb_std,mem_kb_var,mem_kb_min,mem_kb_max"
    awk -F',' '
      NR == 1 { next }
      {
        key = $2 "|" $3
        n[key]++
        t = $6 + 0; m = $7 + 0
        sumT[key]  += t;  sumT2[key] += t * t
        sumM[key]  += m;  sumM2[key] += m * m
        if (!(key in minT) || t < minT[key]) minT[key] = t
        if (!(key in maxT) || t > maxT[key]) maxT[key] = t
        if (!(key in minM) || m < minM[key]) minM[key] = m
        if (!(key in maxM) || m > maxM[key]) maxM[key] = m
        type[key] = $2; name[key] = $3
      }
      END {
        for (k in n) {
          nn = n[k]
          meanT = sumT[k] / nn
          varT  = (nn > 1) ? (sumT2[k] - nn * meanT * meanT) / (nn - 1) : 0
          if (varT < 0) varT = 0
          stdT = sqrt(varT)

          meanM = sumM[k] / nn
          varM  = (nn > 1) ? (sumM2[k] - nn * meanM * meanM) / (nn - 1) : 0
          if (varM < 0) varM = 0
          stdM = sqrt(varM)

          printf "%s,%s,%d,%.4f,%.4f,%.4f,%d,%d,%.4f,%.4f,%.4f,%d,%d\n", \
                 type[k], name[k], nn, meanT, stdT, varT, minT[k], maxT[k], \
                 meanM, stdM, varM, minM[k], maxM[k]
        }
      }
    ' "$RAW_CSV" | sort -t',' -k1,1 -k2,2
  } > "$SUMMARY_CSV"
}

compute_summary
echo "Resumen estadistico guardado en: $SUMMARY_CSV"
echo ""
echo "=== Vista previa del resumen ==="
column -t -s',' "$SUMMARY_CSV" 2>/dev/null || cat "$SUMMARY_CSV"