package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

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
