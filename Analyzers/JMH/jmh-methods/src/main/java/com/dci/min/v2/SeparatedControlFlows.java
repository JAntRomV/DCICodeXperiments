package com.dci.min.v2;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class SeparatedControlFlows {

    @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
    public int N;

    private int[] _VALUES;

    @Setup
    public void setupValues() {
        _VALUES = new int[N];
        for (int i = 0; i < N; i++) {
            _VALUES[i] = i;
        }
    }

    @Benchmark
    public void ifBlock(Blackhole bh) {
        if ((N % 2) == 0) {
        }
    }

    @Benchmark
    public void ifElseBlock(Blackhole bh) {
        if ((N % 2) == 0) {} else {}
    }

    @Benchmark
    public void forBlock(Blackhole bh) {
        for (int i = 0; i < N; i++) {
        }
    }

    @Benchmark
    public void forEachBlock(Blackhole bh) {
        for (int i : _VALUES) {
        }
    }

    @Benchmark
    public void whileBlock(Blackhole bh) {
        int j = 0;
        while (j <= N) {
            j++;
        }
    }

    @Benchmark
    public void doWhileBlock(Blackhole bh) {
        int k = 0;
        do {
            k++;
        } while (k <= N);
    }

    @Benchmark
    public void switchBlock(Blackhole bh) {
        switch (N % 2) {
            case 0:
                break;
            case 1:
                break;
            default:
        }
    }
}
