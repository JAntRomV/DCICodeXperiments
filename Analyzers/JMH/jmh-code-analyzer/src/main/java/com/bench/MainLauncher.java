package com.bench;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.profile.GCProfiler;
import java.util.Set;
import java.util.HashSet; // Una implementación común de Set

public class MainLauncher {
    public static void main(String[] args) throws Exception {
        String targetPackage = "Code.Java.Abacus.v1.Refactored";
        Set<Class<?>> classes = ClassFinder.getClassesInPackage(targetPackage);

        for (Class<?> clazz : classes) {
            System.out.println("STARTING RUNNING CLASS : " + clazz.getName());

            Options opt = new OptionsBuilder()
                .include(AbacusBenchmark.class.getSimpleName())
                .param("className", clazz.getName())
                .addProfiler(GCProfiler.class) // Activa el análisis de GC
                .jvmArgs("-Xmx4g", "-Xms4g")
                .resultFormat(ResultFormatType.CSV)
                .result("1000/AbacusV1Refactored/results_" + clazz.getSimpleName() + ".csv") 
                .build();

            new Runner(opt).run();
        }
    }
}