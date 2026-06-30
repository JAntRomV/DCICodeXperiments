package com.dci;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.profile.GCProfiler;
import java.util.Set;
import java.util.HashSet; 
import org.openjdk.jmh.runner.options.TimeValue;

public class MainLauncher {
    private static final String[] MIN_CLASSES = {
            // "com.dci.min.AlphabetConcatenatorBenchmark",
            "com.dci.min.ControlFlows",
            "com.dci.min.ControlDoWhileFlow",
            "com.dci.min.ControlForEachFlow",
            "com.dci.min.ControlForFlow",
            "com.dci.min.ControlIfElseFlow",
            "com.dci.min.ControlIfFlow",
            "com.dci.min.ControlSwitchFlow",
            "com.dci.min.ControlWhileFlow",
            // "com.dci.min.DoWhileLoop",
            // "com.dci.min.ForEachLoop",
            // "com.dci.min.ForLoop",
            // "com.dci.min.IfElseStatement",
            // "com.dci.min.IfStatement",
            // "com.dci.min.SwitchStatement",
            // "com.dci.min.WhileLoop",
            // "com.dci.min.Program001"
    };

    public static void main(String[] args) throws Exception {

        for (String className : MIN_CLASSES) {
            System.out.println("Running benchmark for: " + className);
            runBenchmark(className);
        }
    }

    private static void runBenchmark(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        Object instance = clazz.getDeclaredConstructor().newInstance();
        String instanceName = instance.getClass().getSimpleName();

        Options opt = new OptionsBuilder()
                .include(instanceName)
                .addProfiler(GCProfiler.class)
                .jvmArgs("-Xmx4g", "-Xms4g")
                .resultFormat(ResultFormatType.CSV)
                .result("100/results_" + instanceName + ".csv") 
                .warmupIterations(5)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(100)
                .measurementTime(TimeValue.seconds(1))
                .forks(1)
                .threads(1)
                .mode(org.openjdk.jmh.annotations.Mode.All)
                .timeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
                .build();

        new Runner(opt).run();
    }
}