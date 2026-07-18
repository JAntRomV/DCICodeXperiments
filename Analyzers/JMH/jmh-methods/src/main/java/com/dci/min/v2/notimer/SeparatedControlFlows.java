package com.dci.min.v2.notimer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.annotations.Level;

@State(Scope.Benchmark)
public class SeparatedControlFlows {

    @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
    public int N;

    private int[] _VALUES;

    @Setup(Level.Iteration)
    public void setupValues(IterationParams params) {
        initVALUES();
    }

    @Benchmark
    public void ifBlock(Blackhole bh) {
        if ((N % 2) == 0) {
        }

        bh.consume(_VALUES);
    }

    @Benchmark
    public void ifElseBlock(Blackhole bh) {
        if ((N % 2) == 0) {
        } else {
        }

        bh.consume(_VALUES);
    }

    @Benchmark
    public void forBlock(Blackhole bh) {
        for (int i = 0; i < N; i++) {
        }

        bh.consume(_VALUES);
    }

    @Benchmark
    public void forEachBlock(Blackhole bh) {
        for (int i : _VALUES) {
        }

        bh.consume(_VALUES);
    }

    @Benchmark
    public void whileBlock(Blackhole bh) {
        int j = 0;
        while (j <= N) {
            j++;
        }

        bh.consume(_VALUES);
    }

    @Benchmark
    public void doWhileBlock(Blackhole bh) {
        int k = 0;
        do {
            k++;
        } while (k <= N);

        bh.consume(_VALUES);
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

        bh.consume(_VALUES);
    }

    private void initVALUES() {
        _VALUES = new int[N];
        for (int i = 0; i < N; i++) {
            _VALUES[i] = i;
        }
    }
}
