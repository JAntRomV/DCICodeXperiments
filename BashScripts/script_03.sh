#!/bin/bash

# Nombre de la carpeta por origen y version
FOLDER_SOURCE="Minimalist/v1/Rare"

# Carpeta donde están los .class
CARPETA_CLASES="./Code/${FOLDER_SOURCE}"

# Archivo de promedios general
promedios_csv="./Data/Times/${FOLDER_SOURCE}/promedios.csv"

# Crear encabezado si no existe
if [ ! -f "$promedios_csv" ]; then
    echo "Clase,Promedio_Tiempo(s),Promedio_Memoria(KB)" > "$promedios_csv"
fi

# Iterar sobre cada archivo .class
for filepath in "$CARPETA_CLASES"/*.class
do
    # Obtener nombre sin extensión
    CLASE=$(basename "$filepath" .class)

    echo "🚀 Ejecutando clase: $CLASE (100 veces)"
    resultados_csv="./Data/Times/${FOLDER_SOURCE}/resultados_${CLASE}.csv"
    echo "Iteración,Tiempo(s),Memoria(KB)" > "$resultados_csv"

    suma_tiempo=0
    suma_memoria=0

    for i in {1..100}
    do
        output_and_metrics=$( { /usr/bin/time -f "%e,%M" java -Xms256m -Xmx2g Code.Minimalist.v1.Rare."$CLASE"; } 2>&1 )

        program_output=$(echo "$output_and_metrics" | head -n -1)
        metrics_line=$(echo "$output_and_metrics" | tail -n 1)

        tiempo=$(echo "$metrics_line" | cut -d',' -f1)
        memoria=$(echo "$metrics_line" | cut -d',' -f2)

        echo "Iteración $i ($CLASE):"
        echo "$program_output"
        echo "Tiempo = ${tiempo}s, Memoria = ${memoria}KB"
        echo "-----------------------------"

        echo "$i,$tiempo,$memoria" >> "$resultados_csv"

        suma_tiempo=$(echo "$suma_tiempo + $tiempo" | bc)
        suma_memoria=$(echo "$suma_memoria + $memoria" | bc)
    done

    promedio_tiempo=$(echo "scale=4; $suma_tiempo / 100" | bc)
    promedio_memoria=$(echo "$suma_memoria / 100" | bc)

    echo "$CLASE,$promedio_tiempo,$promedio_memoria" >> "$promedios_csv"

    # echo "✅ Resultados para $CLASE guardados en $resultados_csv"
    echo "📊 Promedio agregado a $promedios_csv"
    echo
done
