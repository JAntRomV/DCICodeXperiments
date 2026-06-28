package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;

public class ControlForEachFlow {

    @Param({"0", "1", "3", "5", "10"})
    public int N;

    @Benchmark
    public void exec(Blackhole bh) {
        int total = 0;

        int[] values = new int[N];
        for (int i = 0; i < N; i++) {
            values[i] = i + 1;
        }
        for (int value : values) {
            total += value;
        }

        bh.consume(total);
    }
}
