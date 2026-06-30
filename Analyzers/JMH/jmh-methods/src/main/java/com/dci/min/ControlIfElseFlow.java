package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ControlIfElseFlow {

    @Param({"0", "1", "3", "5", "10"})
    public int N;

    private int _TOTAL = 0;

    @Benchmark
    public void exec(Blackhole bh) {
        
        // if-else
        if (N % 2 == 0) {
            _TOTAL += 2;
        } else {
            _TOTAL += 3;
        }

        bh.consume(_TOTAL);
    }
}
