package com.dci.min.v2.tools;

import org.openjdk.jmh.annotations.Mode;
import java.util.Map;
import java.util.Locale;
import java.util.HashMap;

public class ModeMapper {

    private static final Map<String, Mode> ALIASES = new HashMap<>();

    static {
        // AverageTime
        ALIASES.put("AVGT", Mode.AverageTime);
        ALIASES.put("AVG", Mode.AverageTime);
        ALIASES.put("AVERAGETIME", Mode.AverageTime);

        // Throughput
        ALIASES.put("THRPT", Mode.Throughput);
        ALIASES.put("THROUGHPUT", Mode.Throughput);

        // SampleTime
        ALIASES.put("SAMT", Mode.SampleTime);
        ALIASES.put("SAMPLE", Mode.SampleTime);
        ALIASES.put("SAMPLETIME", Mode.SampleTime);

        // SingleShotTime
        ALIASES.put("SS", Mode.SingleShotTime);
        ALIASES.put("SINGLESHOT", Mode.SingleShotTime);
        ALIASES.put("SINGLESHOTTIME", Mode.SingleShotTime);

        // All
        ALIASES.put("ALL", Mode.All);
    }

    /**
     * Convierte un identificador de texto (ej. "SAMT", "avgt", "Throughput")
     * en el Mode correspondiente de JMH.
     *
     * @param modeParam valor recibido por parámetro (case-insensitive)
     * @return el Mode de JMH correspondiente
     * @throws IllegalArgumentException si el valor no coincide con ningún modo conocido
     */
    public static Mode fromParam(String modeParam) {
        if (modeParam == null || modeParam.isBlank()) {
            throw new IllegalArgumentException("El parametro de Mode no puede ser nulo o vacio");
        }

        String key = modeParam.trim().toUpperCase(Locale.ROOT);
        Mode mode = ALIASES.get(key);

        if (mode == null) {
            throw new IllegalArgumentException(
                "Mode desconocido: '" + modeParam + "'. Valores validos: " + ALIASES.keySet()
            );
        }

        return mode;
    }
}