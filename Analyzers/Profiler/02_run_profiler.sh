#!/usr/bin/env bash
# =============================================================================
# run_profiler.sh
# Ejecutar desde la carpeta raíz: bash DTSCode4Traning/Analyzers/Profiler/run_profiler.sh
# =============================================================================

set -euo pipefail

# ---------------------------------------------------------------------------
# Rutas
# ---------------------------------------------------------------------------
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"   # DTSCode4Traning/
PROFILER_DIR="${ROOT_DIR}/Analyzers/Profiler"
SRC_PACKAGE_PATH="Code/Java/Abacus/v1/Refactored"                       # ruta relativa dentro de ROOT_DIR
SRC_DIR="${ROOT_DIR}/${SRC_PACKAGE_PATH}"
JAVA_PACKAGE="Code.Java.Abacus.v1.Refactored"
BUILD_DIR="${PROFILER_DIR}/build"
AGENT_JAR="${PROFILER_DIR}/ProfilingAgent.jar"
CSV_FILE="${PROFILER_DIR}/profiling_results.csv"
RUNS=100

# ---------------------------------------------------------------------------
# Colores para output
# ---------------------------------------------------------------------------
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; NC='\033[0m'

log()  { echo -e "${CYAN}[INFO]${NC}  $*"; }
ok()   { echo -e "${GREEN}[OK]${NC}    $*"; }
warn() { echo -e "${YELLOW}[WARN]${NC}  $*"; }
err()  { echo -e "${RED}[ERROR]${NC} $*" >&2; }

# ---------------------------------------------------------------------------
# 0. Validaciones previas
# ---------------------------------------------------------------------------
log "Raíz del proyecto: ${ROOT_DIR}"

if [[ ! -d "${SRC_DIR}" ]]; then
    err "No existe el paquete: ${SRC_DIR}"
    err "Verifica que la estructura de carpetas sea: DTSCode4Traning/${SRC_PACKAGE_PATH}/"
    exit 1
fi

command -v java  >/dev/null 2>&1 || { err "java no encontrado en PATH"; exit 1; }
command -v javac >/dev/null 2>&1 || { err "javac no encontrado en PATH"; exit 1; }

JAVA_VERSION=$(java -version 2>&1 | head -1)
log "JVM detectada: ${JAVA_VERSION}"

# ---------------------------------------------------------------------------
# 1. Descubrir todas las clases Java en el paquete
# ---------------------------------------------------------------------------
log "Buscando clases en: ${SRC_DIR}"

mapfile -t JAVA_FILES < <(find "${SRC_DIR}" -maxdepth 1 -name "*.java" | sort)

if [[ ${#JAVA_FILES[@]} -eq 0 ]]; then
    err "No se encontraron archivos .java en ${SRC_DIR}"
    exit 1
fi

ok "Clases encontradas: ${#JAVA_FILES[@]}"
for f in "${JAVA_FILES[@]}"; do
    log "  └── $(basename "${f}")"
done

# Extraer nombres de clase (sin extensión)
CLASS_NAMES=()
for f in "${JAVA_FILES[@]}"; do
    base="$(basename "${f}" .java)"
    CLASS_NAMES+=("${base}")
done

# ---------------------------------------------------------------------------
# 2. Compilar el ProfilingAgent (si no existe el JAR)
# ---------------------------------------------------------------------------
AGENT_SRC="${PROFILER_DIR}/ProfilingAgent.java"
MANIFEST_FILE="${PROFILER_DIR}/agent_manifest.txt"

if [[ ! -f "${AGENT_JAR}" ]]; then
    log "Compilando ProfilingAgent..."

    if [[ ! -f "${AGENT_SRC}" ]]; then
        err "No se encuentra ${AGENT_SRC}. Ejecuta primero setup_agent.sh o crea el agente."
        exit 1
    fi

    mkdir -p "${BUILD_DIR}/agent"
    javac -d "${BUILD_DIR}/agent" "${AGENT_SRC}"

    cat > "${MANIFEST_FILE}" <<EOF
Premain-Class: ${JAVA_PACKAGE}.ProfilingAgent
Agent-Class: ${JAVA_PACKAGE}.ProfilingAgent
Can-Redefine-Classes: true
Can-Retransform-Classes: true
EOF

    jar cfm "${AGENT_JAR}" "${MANIFEST_FILE}" -C "${BUILD_DIR}/agent" .
    ok "ProfilingAgent.jar creado en: ${AGENT_JAR}"
else
    ok "ProfilingAgent.jar ya existe, omitiendo compilación."
fi

# ---------------------------------------------------------------------------
# 3. Compilar las clases del paquete Refactored
# ---------------------------------------------------------------------------
log "Compilando clases del paquete ${JAVA_PACKAGE}..."
mkdir -p "${BUILD_DIR}/classes"

javac -d "${BUILD_DIR}/classes" \
      -sourcepath "${ROOT_DIR}" \
      "${JAVA_FILES[@]}" 2>&1 | while IFS= read -r line; do
    warn "  javac: ${line}"
done

ok "Compilación completada en: ${BUILD_DIR}/classes"

# ---------------------------------------------------------------------------
# 4. Inicializar CSV
# ---------------------------------------------------------------------------
log "Inicializando archivo CSV: ${CSV_FILE}"

CSV_HEADER="class_name,run_number,execution_time_ms,heap_used_before_mb,heap_used_after_mb,heap_max_mb,gc_count_before,gc_count_after,gc_time_ms_before,gc_time_ms_after,cpu_load_percent,threads_live,exit_code,timestamp"
echo "${CSV_HEADER}" > "${CSV_FILE}"
ok "CSV creado con cabecera."

# ---------------------------------------------------------------------------
# 5. Ejecutar cada clase 100 veces con el agente de profiling
# ---------------------------------------------------------------------------
TOTAL_CLASSES=${#CLASS_NAMES[@]}
CLASS_IDX=0

for CLASS_NAME in "${CLASS_NAMES[@]}"; do
    CLASS_IDX=$((CLASS_IDX + 1))
    FULL_CLASS="${JAVA_PACKAGE}.${CLASS_NAME}"
    log "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    log "Clase ${CLASS_IDX}/${TOTAL_CLASSES}: ${FULL_CLASS}"

    RESULTS_FILE="${PROFILER_DIR}/${CLASS_NAME}_metrics.tmp"
    > "${RESULTS_FILE}"   # limpiar temporal

    for RUN in $(seq 1 "${RUNS}"); do
        TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

        # Ejecutar con el agente; el agente escribe una línea CSV al stdout
        SET_EXIT=0
        METRICS=$(java \
            -javaagent:"${AGENT_JAR}" \
            -Dprofiling.class="${CLASS_NAME}" \
            -Dprofiling.run="${RUN}" \
            -cp "${BUILD_DIR}/classes" \
            "${FULL_CLASS}" 2>/dev/null) || SET_EXIT=$?

        # La última línea del output es el CSV de métricas del agente
        METRIC_LINE=$(echo "${METRICS}" | tail -1)

        if [[ -n "${METRIC_LINE}" && "${METRIC_LINE}" == *","* ]]; then
            echo "${METRIC_LINE},${SET_EXIT},${TIMESTAMP}" >> "${RESULTS_FILE}"
        else
            # Si el agente no produjo métricas (clase sin main, etc.), registrar error
            warn "  Run ${RUN}: no se obtuvieron métricas de ${CLASS_NAME}"
            echo "${CLASS_NAME},${RUN},0,0,0,0,0,0,0,0,0,0,${SET_EXIT},${TIMESTAMP}" >> "${RESULTS_FILE}"
        fi

        # Barra de progreso liviana (cada 10 runs)
        if (( RUN % 10 == 0 )); then
            log "  Progreso: ${RUN}/${RUNS} ejecuciones completadas"
        fi
    done

    # Agregar resultados de esta clase al CSV principal
    cat "${RESULTS_FILE}" >> "${CSV_FILE}"
    rm -f "${RESULTS_FILE}"
    ok "  ${CLASS_NAME}: ${RUNS} ejecuciones completadas → datos en CSV"
done

# ---------------------------------------------------------------------------
# 6. Generar reporte de resumen
# ---------------------------------------------------------------------------
SUMMARY_FILE="${PROFILER_DIR}/profiling_summary.txt"

log "Generando resumen estadístico: ${SUMMARY_FILE}"

{
    echo "============================================================"
    echo "  RESUMEN DE PROFILING — $(date)"
    echo "  Paquete : ${JAVA_PACKAGE}"
    echo "  Clases  : ${TOTAL_CLASSES}"
    echo "  Runs    : ${RUNS} por clase"
    echo "  CSV     : ${CSV_FILE}"
    echo "============================================================"
    echo ""

    for CLASS_NAME in "${CLASS_NAMES[@]}"; do
        echo "── ${CLASS_NAME} ──"
        # Leer las filas de esta clase desde el CSV y calcular promedio de execution_time_ms (columna 3)
        awk -F',' -v cls="${CLASS_NAME}" '
            NR>1 && $1==cls {
                sum+=$3; count++; min=($3<min||count==1)?$3:min; max=($3>max)?$3:max
            }
            END {
                if(count>0)
                    printf "  Ejecuciones válidas : %d\n  Tiempo promedio (ms): %.2f\n  Mínimo (ms)         : %.2f\n  Máximo (ms)         : %.2f\n\n", count, sum/count, min, max
                else
                    print "  Sin datos de ejecución\n"
            }
        ' "${CSV_FILE}"
    done
} > "${SUMMARY_FILE}"

cat "${SUMMARY_FILE}"

echo ""
ok "============================================================"
ok " Profiling completado."
ok " CSV       : ${CSV_FILE}"
ok " Resumen   : ${SUMMARY_FILE}"
ok "============================================================"
