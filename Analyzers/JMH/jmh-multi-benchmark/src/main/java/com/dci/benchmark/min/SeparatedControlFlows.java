package com.dci.benchmark.min;

import com.dci.benchmark.profiler.LineProfiler;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Thread)
public class SeparatedControlFlows {

    @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
    public int N;

    private int[] _VALUES;
    private LineProfiler profiler;

    @Setup
    public void setupValues() {
        _VALUES = new int[N];
        for (int i = 0; i < N; i++) {
            _VALUES[i] = i;
        }
        profiler = new LineProfiler("SeparatedControlFlows", String.valueOf(System.nanoTime()));
    }

    @Benchmark
    public void ifBlock(Blackhole bh) {
        profiler.mark("IF-BLOCK-START");
        if ((N % 2) == 0) {
            profiler.mark("IF-BLOCK-TRUE");
        }
        profiler.mark("IF-BLOCK-END");
        bh.consume(profiler);
    }

    @Benchmark
    public void ifElseBlock(Blackhole bh) {
        profiler.mark("IF-ELSE-BLOCK-START");
        if ((N % 2) == 0) {
            profiler.mark("IF-ELSE-IF");
        } else {
            profiler.mark("IF-ELSE-ELSE");
        }
        profiler.mark("IF-ELSE-BLOCK-END");
        bh.consume(profiler);
    }

    @Benchmark
    public void forBlock(Blackhole bh) {
        profiler.mark("FOR-BLOCK-START");
        for (int i = 0; i < N; i++) {
            profiler.mark("FOR-BLOCK-ITERATION:" + i);
        }
        profiler.mark("FOR-BLOCK-END");
        bh.consume(profiler);
    }

    @Benchmark
    public void forEachBlock(Blackhole bh) {
        profiler.mark("FOR-EACH-BLOCK-START");
        for (int i : _VALUES) {
            profiler.mark("FOR-EACH-BLOCK-ITERATION:" + i);
        }
        profiler.mark("FOR-EACH-BLOCK-END");
        bh.consume(profiler);
    }

    @Benchmark
    public void whileBlock(Blackhole bh) {
        profiler.mark("WHILE-BLOCK-START");
        int j = 0;
        profiler.mark("WHILE-J-INIT");
        while (j <= N) {
            profiler.mark("WHILE-BLOCK-ITERATION-BEFORE:" + j);
            j++;
            profiler.mark("WHILE-BLOCK-ITERATION-AFTER:" + j);
        }
        profiler.mark("WHILE-BLOCK-END");
        bh.consume(profiler);
    }

    @Benchmark
    public void doWhileBlock(Blackhole bh) {
        profiler.mark("DO-WHILE-BLOCK-START");
        int k = 0;
        profiler.mark("DO-WHILE-K-INIT");
        do {
            profiler.mark("DO-WHILE-BLOCK-ITERATION-BEFORE:" + k);
            k++;
            profiler.mark("DO-WHILE-BLOCK-ITERATION-AFTER:" + k);
        } while (k <= N);
        profiler.mark("DO-WHILE-BLOCK-END");
        bh.consume(profiler);
    }

    @Benchmark
    public void switchBlock(Blackhole bh) {
        profiler.mark("SWITCH-BLOCK-START");
        switch (N % 2) {
            case 0:
                profiler.mark("SWITCH-CASE-0");
                break;
            case 1:
                profiler.mark("SWITCH-CASE-1");
                break;
            default:
                profiler.mark("SWITCH-DEFAULT");
        }
        profiler.mark("SWITCH-BLOCK-END");
        bh.consume(profiler);
    }

    @TearDown(Level.Invocation)
    public void tearDownInvocation() {
        profiler.flush();
    }
}
