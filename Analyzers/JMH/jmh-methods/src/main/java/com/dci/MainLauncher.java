package com.dci;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.options.TimeValue;

public class MainLauncher {
    private static final String[] MIN_CLASSES = {
            "com.dci.min.v2.ControlFlows"
    };

    public static void main(String[] args) throws Exception {
        int Iterations = Integer.parseInt(args[0]); // Number of iterations for the benchmark
        int FORKS = Integer.parseInt(args[1]); // Number of forks for the benchmark    
        int WarmupIterations = Integer.parseInt(args[2]); // Number of warmup iterations
        int measurementIterations = Iterations / FORKS; // Calculate measurement iterations per fork

        for (String className : MIN_CLASSES) {
            System.out.println("Running benchmark for: " + className);
            runBenchmark(className, Iterations, measurementIterations, WarmupIterations, FORKS);
        }
    }

    private static void runBenchmark(String className, 
        int iterations,
        int measurementIterations, 
        int WarmupIterations, 
        int FORKS) throws Exception {
        // Dynamically load the class and create an instance
        Class<?> clazz = Class.forName(className);
        Object instance = clazz.getDeclaredConstructor().newInstance();
        String instanceName = instance.getClass().getSimpleName();

        // Create a results directory based on the number of iterations and the instance name
        String resultsDirectory = iterations + "/" + instanceName; 
        String argResultsDirectory = "-Dresults.directory=" + resultsDirectory; 
        String resultsJMHCSV = resultsDirectory + "/" +instanceName + "_JMH.csv";

        // Run the benchmark using JMH
        if(CreateResultsDirectory(resultsDirectory)) {    
            Options opt = new OptionsBuilder()
                .include(instanceName)
                .addProfiler(GCProfiler.class)
                // .param("resultsDirectory", resultsDirectory)
                .jvmArgs("-Xmx4g", "-Xms4g",argResultsDirectory)
                .resultFormat(ResultFormatType.CSV)
                .result(resultsJMHCSV) 
                .warmupIterations(WarmupIterations)
                .warmupTime(TimeValue.milliseconds(500))
                .measurementIterations(measurementIterations)
                .measurementTime(TimeValue.milliseconds(500))
                .forks(FORKS)
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