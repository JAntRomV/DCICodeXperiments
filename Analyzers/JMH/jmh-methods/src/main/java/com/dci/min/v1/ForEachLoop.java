package com.dci.min.v1;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import java.util.List;
import java.util.ArrayList; 
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ForEachLoop {

    @Param({"10","100", "1000"})
    public int SIZE;

    private List<Integer> VALUES;

    @Setup
    public void setup() {
        VALUES = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            VALUES.add(i);
        }
    }

    @Benchmark
    public void exec(Blackhole bh) {
        int total = 0;
        for (int value : VALUES) {
            total += value;
        }
        bh.consume(total);
    }
}
