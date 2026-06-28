package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ControlIfFlow {

    @Param({"0", "1", "3", "5", "10"})
    public int N;

    @Benchmark
    public void exec(Blackhole bh) {
        int total = 0;

        if (N == 0) {
            total += 1;
        }

        bh.consume(total);
    }
}
