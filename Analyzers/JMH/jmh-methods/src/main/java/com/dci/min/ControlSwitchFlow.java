package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ControlSwitchFlow {

    @Param({"0", "1", "3", "5", "10"})
    public int N;

    @Benchmark
    public void exec(Blackhole bh) {
        int total = 0;

        switch (N % 4) {
            case 0:
                total += 10;
                break;
            case 1:
                total += 20;
                break;
            case 2:
                total += 30;
                break;
            default:
                total += 40;
                break;
        }

        bh.consume(total);
    }
}
