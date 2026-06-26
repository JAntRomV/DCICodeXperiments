package com.dci;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.profile.GCProfiler;
import java.util.Set;
import java.util.HashSet; 


public class MainLauncher {
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(StringBenchmark.class.getSimpleName())
                .addProfiler(GCProfiler.class)
                .build();

        new Runner(opt).run();
    }
}