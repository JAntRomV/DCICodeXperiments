package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class IfElseStatement {

    @Param({"1","-1","1","-1"})
    public int _VALUE;

    @Benchmark
    public void exec(Blackhole bh) {
        int value = _VALUE;
        if (value > 0) {
            value = 1;
        } else {
            value = -1;
        }
        bh.consume(value);
    }
}
