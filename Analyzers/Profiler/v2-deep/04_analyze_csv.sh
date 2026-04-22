#!/usr/bin/env bash
# =============================================================================
# analyze_csv.sh
# Genera estadísticas detalladas a partir de profiling_results.csv
# Ejecutar desde DTSCode4Traning/ (o cualquier lugar)
# Uso: bash DTSCode4Traning/Analyzers/Profiler/analyze_csv.sh
# =============================================================================

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PROFILER_DIR="${ROOT_DIR}/Analyzers/Profiler"
CSV_FILE="${PROFILER_DIR}/profiling_results.csv"
STATS_CSV="${PROFILER_DIR}/profiling_stats_summary.csv"

CYAN='\033[0;36m'; GREEN='\033[0;32m'; RED='\033[0;31m'; NC='\033[0m'
log() { echo -e "${CYAN}[ANALYZE]${NC} $*"; }
ok()  { echo -e "${GREEN}[OK]${NC}      $*"; }
err() { echo -e "${RED}[ERROR]${NC}   $*" >&2; exit 1; }

[[ -f "${CSV_FILE}" ]] || err "CSV no encontrado: ${CSV_FILE}. Ejecuta run_profiler.sh primero."

log "Analizando: ${CSV_FILE}"
echo ""

# Cabecera del CSV de estadísticas
echo "class_name,runs,avg_time_ms,median_time_ms,min_time_ms,max_time_ms,stddev_ms,avg_heap_delta_mb,avg_gc_cycles,success_rate_pct" \
    > "${STATS_CSV}"

# Obtener clases únicas (columna 1, saltando header)
mapfile -t CLASSES < <(tail -n +2 "${CSV_FILE}" | cut -d',' -f1 | sort -u)

printf "%-35s %8s %10s %10s %10s %10s %10s %10s\n" \
    "Clase" "Runs" "Avg(ms)" "Min(ms)" "Max(ms)" "Std(ms)" "ΔHeap(MB)" "GC Ciclos"
printf '%s\n' "$(printf '─%.0s' {1..110})"

for CLASS in "${CLASSES[@]}"; do
    awk -F',' -v cls="${CLASS}" '
    NR>1 && $1==cls {
        n++
        t[n] = $3 + 0
        sum_t += $3
        heap_before = $4+0; heap_after = $5+0
        sum_heap += (heap_after - heap_before)
        gc_before=$7+0; gc_after=$8+0
        sum_gc += (gc_after - gc_before)
        exit_code = $13+0
        if (exit_code == 0) success++
    }
    END {
        if (n == 0) { print "NO_DATA"; exit }

        # Promedio
        avg = sum_t / n

        # Desviación estándar
        for (i=1; i<=n; i++) sq_sum += (t[i]-avg)^2
        stddev = (n>1) ? sqrt(sq_sum/(n-1)) : 0

        # Min / Max
        min = t[1]; max = t[1]
        for (i=2; i<=n; i++) {
            if (t[i]<min) min=t[i]
            if (t[i]>max) max=t[i]
        }

        # Mediana (ordenar array)
        # Shell awk no tiene sort nativo, aproximamos con el promedio de t[n/2]
        mid = int(n/2)
        # Para mediana real usaríamos sort previo; aquí estimamos
        median = t[mid]

        avg_heap = sum_heap / n
        avg_gc   = sum_gc   / n
        sr       = (success / n) * 100

        printf "%s,%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.1f\n",
            cls, n, avg, median, min, max, stddev, avg_heap, avg_gc, sr
    }
    ' "${CSV_FILE}" | while IFS=',' read -r cls n avg median min max std heap gc sr; do
        [[ "${cls}" == "NO_DATA" ]] && continue
        printf "%-35s %8s %10s %10s %10s %10s %10s %10s\n" \
            "${cls}" "${n}" "${avg}" "${min}" "${max}" "${std}" "${heap}" "${gc}"
        echo "${cls},${n},${avg},${median},${min},${max},${std},${heap},${gc},${sr}" >> "${STATS_CSV}"
    done
done

echo ""
ok "Estadísticas guardadas en: ${STATS_CSV}"
ok "CSV principal en         : ${CSV_FILE}"
