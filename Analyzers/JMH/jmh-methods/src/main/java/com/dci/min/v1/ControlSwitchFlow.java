package com.dci.min.v1;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ControlSwitchFlow {

    @Param({"0", "1", "3", "5", "10"})
    public int N;

    private int _TOTAL = 0;

    @Benchmark
    public void exec(Blackhole bh) {
        
        // switch statement
        switch (N % 4) {
            case 0:
                _TOTAL += 10;
                break;
            case 1:
                _TOTAL += 20;
                break;
            case 2:
                _TOTAL += 30;
                break;
            default:
                _TOTAL += 40;
                break;
        }

        bh.consume(_TOTAL);
    }
}
