package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;

public class AlphabetConcatenatorBenchmark {

    @Param({"0", "1", "3", "4", "10", "26"})
    public int N;

    private String _WORD;

    @Setup
    public void setup() {
        _WORD = AlphabetConcatenator.concatLetters(N);
    }

    @Benchmark
    public void exec(Blackhole bh) {
        String result = _WORD;
        bh.consume(result);
    }
}
