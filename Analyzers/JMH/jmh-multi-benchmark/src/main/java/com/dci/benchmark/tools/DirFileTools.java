package com.dci.benchmark.tools;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DirFileTools {

    public static Map<String, String> getParams(String[] args) {
        Map<String, String> params = new HashMap<>();

        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] parts = arg.substring(2).split(":", 2);
                if (parts.length == 2) {
                    params.put(parts[0], parts[1]);
                }
            }
        }
        return params;
    }

    public static String createBenchmarkInstanceName(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        Object instance = clazz.getDeclaredConstructor().newInstance();
        return instance.getClass().getSimpleName();
    }

    public static String CreateResultsDirectory(int iterations,
        int measurementIterations,
        int forks,
        int warmupIterations) {

        String resultsDirectory = String.format("IT%dMI%dFR%dWI%d", iterations, measurementIterations, forks, warmupIterations);

        try {
            Files.createDirectories(Paths.get(resultsDirectory));
            return resultsDirectory;
        } catch (Exception e) {
            System.err.println("Error creating results directory: " + e.getMessage());
            return null;
        }
    }
}
