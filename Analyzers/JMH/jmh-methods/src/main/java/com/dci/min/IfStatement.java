package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Param;

@State(Scope.Benchmark)
public class IfStatement {

    @Param({"1","0","1","0"})
    public int _VALUE;

    @Benchmark
    public void exec(Blackhole bh) {
        int value = _VALUE;
        if (value > 0) {
            value += 1;
        }
        bh.consume(value);
    }
}
