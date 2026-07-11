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
public class IfFlow {

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
        profiler = new LineProfiler("IfFlow", String.valueOf(System.nanoTime()));
    }

    @Benchmark
    public void exec(Blackhole bh) {
        profiler.mark("IF-START");
        if ((N % 2) == 0) {
            profiler.mark("IF-TRUE");
        }
        profiler.mark("IF-END");
        bh.consume(profiler);
    }

    @TearDown(Level.Invocation)
    public void tearDownInvocation() {
        profiler.flush();
    }
}
