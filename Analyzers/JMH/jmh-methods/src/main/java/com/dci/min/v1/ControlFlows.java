package com.dci.min.v1;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Setup;

@State(Scope.Benchmark)
public class ControlFlows {

    @Param({"0", "1", "3", "5", "10"})
    public int N;

    private int[] _VALUES;
    private int _TOTAL = 0;

    @Setup
    public void setupValues() {
        _VALUES = new int[N];
        for (int i = 0; i < N; i++) {
            _VALUES[i] = i + 1;
        }
    }

    @Benchmark
    public void exec(Blackhole bh) {

        // if simple
        if (N == 0) {
            _TOTAL += 1;
        }

        // if-else
        if (N % 2 == 0) {
            _TOTAL += 2;
        } else {
            _TOTAL += 3;
        }

        // for loop
        for (int i = 0; i < N; i++) {
            _TOTAL += i;
        }

        // while loop
        int j = 0;
        while (j < N) {
            _TOTAL += j * 2;
            j++;
        }

        // do-while loop
        int k = 0;
        if (N > 0) {
            do {
                _TOTAL += k * 3;
                k++;
            } while (k < N);
        }

        // foreach loop
        for (int value : _VALUES) {
            _TOTAL += value;
        }

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
