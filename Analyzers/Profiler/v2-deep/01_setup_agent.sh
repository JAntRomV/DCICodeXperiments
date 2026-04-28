#!/usr/bin/env bash
# =============================================================================
# setup_agent.sh
# Compila ProfilingAgent.java y genera ProfilingAgent.jar
# Ejecutar ANTES de run_profiler.sh si es la primera vez.
# Uso: bash DTSCode4Traning/Analyzers/Profiler/setup_agent.sh
# =============================================================================

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PROFILER_DIR="${ROOT_DIR}/Profiler/v2-deep"
AGENT_SRC="${PROFILER_DIR}/ProfilingAgent.java"
AGENT_JAR="${PROFILER_DIR}/ProfilingAgent.jar"
BUILD_DIR="${PROFILER_DIR}/build/agent"
MANIFEST="${PROFILER_DIR}/agent_manifest.txt"
JAVA_PACKAGE="Code.Java.Abacus.v1.Rare"

RED='\033[0;31m'; GREEN='\033[0;32m'; CYAN='\033[0;36m'; NC='\033[0m'
log() { echo -e "${CYAN}[SETUP]${NC} $*"; }
ok()  { echo -e "${GREEN}[OK]${NC}    $*"; }
err() { echo -e "${RED}[ERROR]${NC} $*" >&2; exit 1; }

command -v java  >/dev/null 2>&1 || err "java no encontrado en PATH"
command -v javac >/dev/null 2>&1 || err "javac no encontrado en PATH"
command -v jar   >/dev/null 2>&1 || err "jar no encontrado en PATH"

[[ -f "${AGENT_SRC}" ]] || err "No se encuentra: ${AGENT_SRC}"

log "Compilando ProfilingAgent.java..."
mkdir -p "${BUILD_DIR}"

# El paquete requiere estructura de directorios
PACKAGE_BUILD_DIR="${BUILD_DIR}/$(echo "${JAVA_PACKAGE}" | tr '.' '/')"
mkdir -p "$(dirname "${PACKAGE_BUILD_DIR}")"

javac \
    -source 11 -target 11 \
    -d "${BUILD_DIR}" \
    "${AGENT_SRC}"

ok "Compilación exitosa."

# Manifest del agente
cat > "${MANIFEST}" <<EOF
Premain-Class: ${JAVA_PACKAGE}.ProfilingAgent
Agent-Class: ${JAVA_PACKAGE}.ProfilingAgent
Can-Redefine-Classes: true
Can-Retransform-Classes: true
EOF

log "Empaquetando ProfilingAgent.jar..."
jar cfm "${AGENT_JAR}" "${MANIFEST}" -C "${BUILD_DIR}" .

ok "ProfilingAgent.jar generado en: ${AGENT_JAR}"
log ""
log "Ahora puedes ejecutar:"
log "  bash DTSCode4Traning/Analyzers/Profiler/run_profiler.sh"
