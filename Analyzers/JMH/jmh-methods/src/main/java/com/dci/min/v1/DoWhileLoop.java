package com.dci.min.v1;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
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