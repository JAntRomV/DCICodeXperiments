package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Param;

public class ForLoop {

    @Param({"10","100", "1000"})
    public int N;

    @Benchmark
    public void exec(Blackhole bh) {
        int total = 0;
        for (int i = 0; i < N; i++) {
            total += i;
        }
        bh.consume(total);
    }
}
