package com.dci.benchmark.tools;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.options.TimeValue;
import java.util.List;
import com.dci.benchmark.profiler.SummaryGenerator;
import com.dci.benchmark.tools.DirFileTools;
import org.openjdk.jmh.runner.RunnerException;

public class JMHRunner {

    public static void runBenchmark(List<String> targerClasses,
        int iterations,
        int measurementIterations,
        int warmupIterations,
        int forks,
        int minHeap,
        int maxHeap) throws RunnerException {

        String Xms = String.format("-Xms%dm", minHeap);
        String Xmx = String.format("-Xmx%dm", maxHeap);

        OptionsBuilder builder = new OptionsBuilder();

        for (String clazz : targerClasses) {
            builder.include(clazz);
        }

        Options opt = builder
                .addProfiler(GCProfiler.class)
                .jvmArgs(Xmx, Xms)
                .resultFormat(ResultFormatType.CSV)
                .result("results/jmh_results.csv")     
                .warmupIterations(warmupIterations)
                .warmupTime(TimeValue.milliseconds(500))    
                .measurementIterations(measurementIterations)
                .measurementTime(TimeValue.milliseconds(500))     
                .forks(forks)
                .threads(1)        
                .mode(org.openjdk.jmh.annotations.Mode.SampleTime)
                .timeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();

        System.out.println("Benchmark de JMH finalizado. Resultados en results/jmh_results.csv");
        System.out.println("Generando resumen de perfilado línea por línea...");

        SummaryGenerator.generate("results/line_profile.csv", "results/line_profile_summary.csv");
    }
}
