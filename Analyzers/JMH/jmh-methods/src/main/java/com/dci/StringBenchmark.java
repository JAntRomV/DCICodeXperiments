package com.dci;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class StringBenchmark {

    @Param({"10", "100"})
    public int iterations;

    @Benchmark
    public void testStringPlus(Blackhole bh) {
        String str = "";
        for (int i = 0; i < iterations; i++) {
            str += "data";
        }
        bh.consume(str);
    }

    @Benchmark
    public void testStringBuilder(Blackhole bh) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("data");
        }
        bh.consume(sb.toString());
    }
}
