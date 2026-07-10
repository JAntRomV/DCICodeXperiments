package com.dci.min.v2.tools;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.options.TimeValue;

public class JMHRunner {

    public static void runBenchmark(String className,
        int iterations,
        int measurementIterations,
        int warmupIterations,
        int forks,
        int minHeap,
        int maxHeap) throws Exception {

        String Xms = String.format("-Xms%dm", minHeap);
        String Xmx = String.format("-Xmx%dm", maxHeap);

        String instanceName = DirFileTools.createBenchmarkInstanceName(className);
        String resultsDirectory = DirFileTools.CreateResultsDirectory(instanceName, iterations, measurementIterations, forks, warmupIterations);
        String resultsJMHCSV = String.format("%s/%s_JMH.csv", resultsDirectory, instanceName);

        Options opt = new OptionsBuilder()
            .include(instanceName)
            .addProfiler(GCProfiler.class)
            .jvmArgs(Xms, Xmx)
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
