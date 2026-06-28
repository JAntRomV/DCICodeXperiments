package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ControlFlows {

    @Param({"0", "1", "3", "5", "10"})
    public int N;

    @Benchmark
    public void exec(Blackhole bh) {
        int total = 0;

        // if simple
        if (N == 0) {
            total += 1;
        }

        // if-else
        if (N % 2 == 0) {
            total += 2;
        } else {
            total += 3;
        }

        // for loop
        for (int i = 0; i < N; i++) {
            total += i;
        }

        // while loop
        int j = 0;
        while (j < N) {
            total += j * 2;
            j++;
        }

        // do-while loop
        int k = 0;
        if (N > 0) {
            do {
                total += k * 3;
                k++;
            } while (k < N);
        }

        // foreach loop
        int[] values = new int[N];
        for (int i = 0; i < N; i++) {
            values[i] = i + 1;
        }
        for (int value : values) {
            total += value;
        }

        // switch statement
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
