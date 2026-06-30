package com.dci.min.v1;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ControlForFlow {

    @Param({"0", "1", "3", "5", "10"})
    public int N;

    private int _TOTAL = 0;

    @Benchmark
    public void exec(Blackhole bh) {
        
        // for loop
        for (int i = 0; i < N; i++) {
            _TOTAL += i;
        }

        bh.consume(_TOTAL);
    }
}
