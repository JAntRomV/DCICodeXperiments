package com.dci.min.v2.tools;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;

import java.util.List;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.options.TimeValue;

public class JMHRunner {

    public static void runBenchmark(int iterations,
        int measurementIterations,
        int warmupIterations,
        int forks,
        int minHeap,
        int maxHeap,
        String[] targerClasses) throws Exception {

        String Xms = String.format("-Xms%dm", minHeap);
        String Xmx = String.format("-Xmx%dm", maxHeap);

        // String instanceName = DirFileTools.createBenchmarkInstanceName(className);
        String resultsDirectory = DirFileTools.CreateResultsDirectory(iterations, measurementIterations, forks, warmupIterations);
        // String resultsJMHCSV = String.format("%s/%s_JMH.csv", resultsDirectory, instanceName);
        String resultsJMHCSV = String.format("%s/results_JMH.csv", resultsDirectory);
        String argResultsDirectory = "-Dresults.directory=" + resultsDirectory; 

        OptionsBuilder builder = new OptionsBuilder();

        for (String clazz : targerClasses) {
            builder.include(clazz);
        }

        //Options opt = new OptionsBuilder()
        Options opt = builder
            // .include(instanceName)
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
            .mode(org.openjdk.jmh.annotations.Mode.SampleTime)
            .timeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
            .build();
        new Runner(opt).run();
    }
}
