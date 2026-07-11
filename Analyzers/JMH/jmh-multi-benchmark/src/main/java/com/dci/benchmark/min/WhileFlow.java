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
public class WhileFlow {

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
        profiler = new LineProfiler("WhileFlow", String.valueOf(System.nanoTime()));
    }

    @Benchmark
    public void exec(Blackhole bh) {
        profiler.mark("WHILE-START");
        int j = 0;
        profiler.mark("WHILE-J-INIT");
        while (j <= N) {
            profiler.mark("WHILE-ITERATION-BEFORE:" + j);
            j++;
            profiler.mark("WHILE-ITERATION-AFTER:" + j);
        }
        profiler.mark("WHILE-END");
        bh.consume(profiler);
    }

    @TearDown(Level.Invocation)
    public void tearDownInvocation() {
        profiler.flush();
    }
}
