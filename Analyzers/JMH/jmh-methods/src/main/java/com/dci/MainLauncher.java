package com.dci;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.options.TimeValue;

public class MainLauncher {
    private static final String[] MIN_CLASSES = {
            "com.dci.min.v2.ControlFlows",
            "com.dci.min.v2.ControlFlowsSeparated",
            "com.dci.min.v2.IfFlow",
            "com.dci.min.v2.IfElseFlow",
            "com.dci.min.v2.ForFlow",
            "com.dci.min.v2.ForEachFlow",
            "com.dci.min.v2.WhileFlow",
            "com.dci.min.v2.DoWhileFlow",
            "com.dci.min.v2.SwitchFlow"
    };

    public static void main(String[] args) throws Exception {

        Map<String, String> params = getParams(args);

        int Iterations = Integer.parseInt(params.getOrDefault("I", "100"));         //Default 100 iterations
        int WarmupIterations = Integer.parseInt(params.getOrDefault("WI", "10"));   //Default 10 warmup iterations
        int Forks = Integer.parseInt(params.getOrDefault("F", "4"));                //Default 4 forks
        int MinHeap = Integer.parseInt(params.getOrDefault("MINH", "4096"));        //Min heap en MB
        int MaxHeap = Integer.parseInt(params.getOrDefault("MAXH", "4096"));        //Max heap en MB
        int measurementIterations = Iterations / Forks;                             //Calculate measurement iterations per fork

        String arguments = String.format("Iterations: %d, WarmupIterations: %d, Forks: %d, MinHeap: %d MB, MaxHeap: %d MB, MeasurementIterations: %d", 
            Iterations, WarmupIterations, Forks, MinHeap, MaxHeap, measurementIterations);
        System.out.println("Starting benchmark with parameters: " + arguments);
        for (String className : MIN_CLASSES) {
            System.out.println("Running benchmark for: " + className);
            runBenchmark(className, Iterations, measurementIterations, WarmupIterations, Forks, MinHeap, MaxHeap);
        }
        System.out.println("Completed benchmark for parameters: " + arguments);
    }

    private static Map<String, String> getParams(String[] args) {
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

    private static void runBenchmark(String className, 
        int iterations,
        int measurementIterations, 
        int warmupIterations, 
        int forks,
        int minHeap,
        int maxHeap) throws Exception {

        // Dynamically load the class and create an instance
        Class<?> clazz = Class.forName(className);
        Object instance = clazz.getDeclaredConstructor().newInstance();
        String instanceName = instance.getClass().getSimpleName();

        // Create a results directory based on the number of iterations and the instance name
        String parentDirectory = String.format("IT%dMI%dFR%dWI%d", iterations, measurementIterations, forks, warmupIterations);
        String Xms = String.format("-Xms%dm", minHeap);
        String Xmx = String.format("-Xmx%dm", maxHeap); 
        String resultsDirectory = parentDirectory + "/" + instanceName; 
        String argResultsDirectory = "-Dresults.directory=" + resultsDirectory; 
        String resultsJMHCSV = resultsDirectory + "/" +instanceName + "_JMH.csv";

        // Run the benchmark using JMH
        if(CreateResultsDirectory(resultsDirectory)) {    
            Options opt = new OptionsBuilder()
                .include(instanceName)
                .addProfiler(GCProfiler.class)
                .jvmArgs(Xms, Xmx, argResultsDirectory)
                .resultFormat(ResultFormatType.CSV)
                .result(resultsJMHCSV) 
                .warmupIterations(warmupIterations)
                .warmupTime(TimeValue.milliseconds(500))
                .measurementIterations(measurementIterations)
                .measurementTime(TimeValue.milliseconds(500))
                .forks(forks)
                .threads(1)
                .mode(org.openjdk.jmh.annotations.Mode.All)
                .timeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
                .build();

            new Runner(opt).run();
        }
    }

    private static boolean CreateResultsDirectory(String pathresults) {
        try {
            Files.createDirectories(Paths.get(pathresults));
            return true;
        } catch (Exception e) {
            System.err.println("Error creating results directory: " + e.getMessage());
            return false;
        }
    }
}