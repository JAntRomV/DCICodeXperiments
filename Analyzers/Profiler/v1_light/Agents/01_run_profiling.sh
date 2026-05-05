#!/usr/bin/env bash
set -euo pipefail

CLASSPATH="out/production/:lib/*"
CLASSES_FILE="./Analyzers/Profiler/v1_light/1000/Refactored/classes_to_profile.txt"
OUTPUT_DIR="profiling-output-root"
ITERATIONS=1000
DURATION_SECS=10          # segundos máx. de ejecución por clase (ajusta a tu caso)

mkdir -p "$OUTPUT_DIR"

mapfile -t CLASSES < "$CLASSES_FILE"
echo "Clases encontradas: ${#CLASSES[@]}"

for CLASS in "${CLASSES[@]}"; do
    echo ""
    echo "========================================"
    echo "Clase: $CLASS"
    echo "========================================"

    # Nombre seguro para archivos (reemplazar puntos)
    SAFE_NAME="${CLASS//./_}"
    CLASS_DIR="$OUTPUT_DIR/$SAFE_NAME"
    mkdir -p "$CLASS_DIR"

    for i in $(seq 1 $ITERATIONS); do
        JFR_FILE="$CLASS_DIR/run_$(printf '%03d' $i).jfr"

        echo -n "  Iteración $i/$ITERATIONS... "

        timeout "$DURATION_SECS" java \
            -cp "$CLASSPATH" \
            -XX:+UnlockDiagnosticVMOptions \
            -XX:+FlightRecorder \
            -XX:StartFlightRecording=\
"duration=${DURATION_SECS}s,"\
"filename=${JFR_FILE},"\
"settings=profile,"\
"name=run_${i}" \
            -Xss512k \
            -Xmx256m \
            "$CLASS" 2>/dev/null || true

        echo "OK → $JFR_FILE"
    done

    echo "  Clase $CLASS completada."
done

echo ""
echo "Profiling completo. Archivos en: $OUTPUT_DIR/"