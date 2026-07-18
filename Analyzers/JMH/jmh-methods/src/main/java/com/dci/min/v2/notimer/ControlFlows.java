package com.dci.min.v2.notimer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Level;

@State(Scope.Benchmark)
public class ControlFlows {

    @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
    public int N;

    private int[] _VALUES;

    @Setup(Level.Iteration)
    public void setupValues(IterationParams params) {
        initVALUES();
    }
    
    @Benchmark
    public void exec(Blackhole bh) {
        // IF
        if ((N % 2) == 0) {
        }

        //IF ELSE
        if ((N % 2) == 0) {
        } else {
        }

        //FOR
        for (int i = 0; i < N; i++) {
        }

        //FOR EACH
        for (int i : _VALUES) {
        }

        //WHILE
        int j = 0;
        while (j <= N) {
            j++;
        }

        //DO WHILE
        int k = 0;
        do {
            k++;
        } while (k <= N);

        //SWITCH
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
        // Initialize the _VALUES array with values from 0 to N
        _VALUES = new int[N];
        for (int i = 0; i < N; i++) {
            _VALUES[i] = i;
        }
    }
}
