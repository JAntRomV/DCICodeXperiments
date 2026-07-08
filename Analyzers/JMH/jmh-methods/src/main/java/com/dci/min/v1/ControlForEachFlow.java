package com.dci.min.v1;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ControlForEachFlow {

    @Param({"0", "1", "3", "5", "10"})
    public int N;

    private int[] _VALUES;
    private int _TOTAL = 0;

    @Setup
    public void setupValues() {
        _VALUES = new int[N];
        for (int i = 0; i < N; i++) {
            _VALUES[i] = i + 1;
        }
    }

    @Benchmark
    public void exec(Blackhole bh) {
        
        // foreach loop
        for (int value : _VALUES) {
            _TOTAL += value;
        }

        bh.consume(_TOTAL);
    }
}
