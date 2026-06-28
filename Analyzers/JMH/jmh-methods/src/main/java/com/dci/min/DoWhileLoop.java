package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Param;

public class DoWhileLoop {

    @Param({"10","100", "1000"})
    public int N;

    @Benchmark
    public void exec(Blackhole bh) {
        int i = 0;
        do {
            i++;
        } while (i < N);
        bh.consume(i);
    }
}