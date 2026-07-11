package com.dci.benchmark.profiler;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lee el CSV línea-por-línea (class_id, tag, duration_ns, ...) y genera un
 * resumen agrupado por (clase, tag) con: cantidad de muestras, promedio,
 * mínimo, máximo y percentiles 50/90/95/99, todo en nanosegundos.
 */
public final class SummaryGenerator {

    private SummaryGenerator() {
    }

    public static void generate(String inputCsv, String outputCsv) {
        Path input = Paths.get(inputCsv);
        if (!Files.exists(input)) {
            System.err.println("No existe el archivo de entrada: " + inputCsv);
            return;
        }

        Map<String, List<Long>> groups = new LinkedHashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(input)) {
            String line = reader.readLine(); // descarta encabezado
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] cols = line.split(",", -1);
                if (cols.length < 6) continue;
                String classId = cols[1];
                String tag = cols[3];
                long durationNs;
                try {
                    durationNs = Long.parseLong(cols[5]);
                } catch (NumberFormatException e) {
                    continue;
                }
                String key = classId + "|" + tag;
                groups.computeIfAbsent(key, k -> new ArrayList<>()).add(durationNs);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo CSV de perfilado: " + inputCsv, e);
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(outputCsv))) {
            pw.println("class_id,tag,count,avg_ns,min_ns,max_ns,p50_ns,p90_ns,p95_ns,p99_ns");
            for (Map.Entry<String, List<Long>> entry : groups.entrySet()) {
                String[] parts = entry.getKey().split("\\|", 2);
                List<Long> values = entry.getValue();
                Collections.sort(values);

                long count = values.size();
                double avg = values.stream().mapToLong(Long::longValue).average().orElse(0);
                long min = values.get(0);
                long max = values.get(values.size() - 1);
                long p50 = percentile(values, 50);
                long p90 = percentile(values, 90);
                long p95 = percentile(values, 95);
                long p99 = percentile(values, 99);

                pw.printf("%s,%s,%d,%.2f,%d,%d,%d,%d,%d,%d%n",
                        parts[0], parts[1], count, avg, min, max, p50, p90, p95, p99);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error escribiendo resumen: " + outputCsv, e);
        }

        System.out.println("Resumen generado en " + outputCsv);
    }

    private static long percentile(List<Long> sortedValues, double percentile) {
        if (sortedValues.isEmpty()) return 0;
        int index = (int) Math.ceil(percentile / 100.0 * sortedValues.size()) - 1;
        index = Math.max(0, Math.min(index, sortedValues.size() - 1));
        return sortedValues.get(index);
    }
}
