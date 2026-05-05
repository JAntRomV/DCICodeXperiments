#!/usr/bin/env bash
set -euo pipefail

# ─── Configuración ───────────────────────────────────────────────
PACKAGE="Code.Java.Abacus.v1.Rare"
CLASSPATH="."                          # Ajusta si tienes un JAR: "./miapp.jar"
# RUNS=100
RUNS=1000
CSV="Analyzers/Time-Ram/Abacus_v1/1000/resultados_benchmark_Rare.csv"
PACKAGE_DIR=$(echo "$PACKAGE" | tr '.' '/')
# ─────────────────────────────────────────────────────────────────

# PASO 1 — Descubrir las 100 clases del paquete
echo ">> Buscando clases en: $PACKAGE_DIR"
mapfile -t CLASES < <(
  find . -path "*/${PACKAGE_DIR}/*.class" ! -name '*$*' -type f \
    | sed 's|.*/||; s|\.class||'
)

if [[ ${#CLASES[@]} -eq 0 ]]; then
  echo "ERROR: No se encontraron clases en el paquete $PACKAGE" >&2
  exit 1
fi
echo ">> ${#CLASES[@]} clases encontradas."

# PASO 2 — Crear CSV con cabecera
echo "clase,ejecucion,tiempo_ms,memoria_kb" > "$CSV"

# PASO 3 — Ejecutar cada clase 100 veces
TOTAL_CLASES=${#CLASES[@]}
CLASE_NUM=0

for CLASE in "${CLASES[@]}"; do
  CLASE_NUM=$((CLASE_NUM + 1))
  FQCN="${PACKAGE}.${CLASE}"
  echo "▶ [$CLASE_NUM/$TOTAL_CLASES] $CLASE"

  for RUN in $(seq 1 $RUNS); do

    # Archivo temporal para capturar stderr de /usr/bin/time
    TMP=$(mktemp)

    # /usr/bin/time -v   → memoria en KB (Maximum resident set size)
    # Ejecutamos java; stdout de la clase se descarta (puedes quitar 2>/dev/null)
    /usr/bin/time -v java -cp "$CLASSPATH" "$FQCN" > /dev/null 2>"$TMP" || true

    # Extraer tiempo de ejecución en segundos → convertir a ms
    ELAPSED_SEC=$(grep "Elapsed (wall clock) time" "$TMP" \
      | awk '{print $NF}' \
      | awk -F: '{
          if (NF==3) print ($1*3600 + $2*60 + $3)*1000
          else       print ($1*60   + $2)*1000
        }')

    # Extraer memoria máxima en KB
    MEM_KB=$(grep "Maximum resident set size" "$TMP" | awk '{print $NF}')

    rm -f "$TMP"

    # Redondear tiempo a entero
    TIEMPO_MS=$(printf "%.0f" "${ELAPSED_SEC:-0}")
    MEM_KB=${MEM_KB:-0}

    # Escribir fila en CSV
    echo "${CLASE},${RUN},${TIEMPO_MS},${MEM_KB}" >> "$CSV"

  done

  echo "   ✓ 100 runs completados"
done

echo ""
echo "✅ CSV generado: $CSV  ($(wc -l < "$CSV") filas)"