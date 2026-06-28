package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Param;

@State(Scope.Benchmark)
public class WhileLoop {
    
    @Param({"10","100", "1000"})
    public int N;
    
    @Benchmark
    public void exec(Blackhole bh) {
        int i = 0;
        while (i < N) {
            i++;
        }
        bh.consume(i);
    }
}
