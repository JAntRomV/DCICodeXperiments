#!/usr/bin/env bash
# =============================================================================
# open_visualvm.sh
# Abre VisualVM para inspección visual adicional (opcional).
# El profiling automático lo realiza run_profiler.sh con el agente Java.
# Ejecutar desde: DTSCode4Traning/
# Uso: bash DTSCode4Traning/Analyzers/Profiler/open_visualvm.sh [PID]
# =============================================================================

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PROFILER_DIR="${ROOT_DIR}/Analyzers/Profiler"
CSV_FILE="${PROFILER_DIR}/profiling_results.csv"

CYAN='\033[0;36m'; YELLOW='\033[1;33m'; GREEN='\033[0;32m'; NC='\033[0m'
log()  { echo -e "${CYAN}[VISUALVM]${NC} $*"; }
warn() { echo -e "${YELLOW}[INFO]${NC}     $*"; }
ok()   { echo -e "${GREEN}[OK]${NC}       $*"; }

# ---------------------------------------------------------------------------
# Localizar jvisualvm en el JDK
# ---------------------------------------------------------------------------
JVISUALVM=""

# Candidatos habituales
for candidate in \
    "$(which jvisualvm 2>/dev/null || true)" \
    "${JAVA_HOME:-}/bin/jvisualvm" \
    "/usr/bin/jvisualvm" \
    "/usr/local/bin/jvisualvm" \
    "$(dirname "$(which java 2>/dev/null || true)")/jvisualvm" \
    "/Applications/VisualVM.app/Contents/MacOS/visualvm"; do
    if [[ -x "${candidate}" ]]; then
        JVISUALVM="${candidate}"
        break
    fi
done

if [[ -z "${JVISUALVM}" ]]; then
    warn "jvisualvm no encontrado automáticamente."
    warn "Descarga VisualVM desde: https://visualvm.github.io/"
    warn ""
    warn "Para importar el CSV manualmente en VisualVM:"
    warn "  1. File → Load → selecciona ${CSV_FILE}"
    warn "  2. O usa el plugin 'VisualVM-BufferMonitor' para datos CSV."
    exit 0
fi

ok "VisualVM encontrado: ${JVISUALVM}"

# ---------------------------------------------------------------------------
# Modo 1: conectar a un PID específico
# ---------------------------------------------------------------------------
if [[ $# -ge 1 && "$1" =~ ^[0-9]+$ ]]; then
    TARGET_PID="$1"
    log "Conectando a PID: ${TARGET_PID}"
    "${JVISUALVM}" --openpid "${TARGET_PID}" &
    ok "VisualVM abierto apuntando al proceso ${TARGET_PID}."
    exit 0
fi

# ---------------------------------------------------------------------------
# Modo 2: abrir VisualVM para monitoreo JMX durante run_profiler.sh
# ---------------------------------------------------------------------------
log "Iniciando VisualVM en modo standalone..."
warn "Nota: el profiling automático (CSV) no requiere VisualVM abierto."
warn "VisualVM es útil para inspección visual en tiempo real."
warn ""
warn "Para conectar a una ejecución con JMX, añade estas flags al java en run_profiler.sh:"
warn "  -Dcom.sun.management.jmxremote"
warn "  -Dcom.sun.management.jmxremote.port=9010"
warn "  -Dcom.sun.management.jmxremote.authenticate=false"
warn "  -Dcom.sun.management.jmxremote.ssl=false"
warn ""
warn "Luego en VisualVM: File → Add JMX Connection → localhost:9010"

"${JVISUALVM}" &
ok "VisualVM iniciado (PID: $!)."
log "CSV disponible en: ${CSV_FILE}"
