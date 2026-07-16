package com.dci.min.v2;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.runner.IterationType;
import org.openjdk.jmh.annotations.Level;
import com.dci.min.v2.tools.TimeLogger;

@State(Scope.Benchmark)
public class SeparatedControlFlows {

    @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
    public int N;

    private int[] _VALUES;
    private TimeLogger _timeLogger = new TimeLogger();
    private String _resultsDirectory;

    @Setup(Level.Iteration)
    public void setupValues(IterationParams params) {
        initVALUES();

        _resultsDirectory = System.getProperty("results.directory", "");

        getIsWarmup(params);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        _timeLogger.toCSV(String.format("%s/%s_%d.csv", _resultsDirectory, this.getClass().getSimpleName(), N));
    }

    @Benchmark
    public void ifBlock(Blackhole bh) {
        _timeLogger.logTime("IF-START", true);
        if ((N % 2) == 0) {
            _timeLogger.logTime("IF-TRUE");
        }
        _timeLogger.logTime("IF-END");

        bh.consume(_timeLogger);
    }

    @Benchmark
    public void ifElseBlock(Blackhole bh) {
        _timeLogger.logTime("IFELSE-START", true);
        if ((N % 2) == 0) {
            _timeLogger.logTime("IFELSE-TRUE");
        } else {
            _timeLogger.logTime("IFELSE-FALSE");
        }
        _timeLogger.logTime("IFELSE-END");

        bh.consume(_timeLogger);
    }

    @Benchmark
    public void forBlock(Blackhole bh) {
        _timeLogger.logTime("FOR-START", true);
        for (int i = 0; i < N; i++) {
            _timeLogger.logTime("FOR-ITERATION:" + i);
        }
        _timeLogger.logTime("FOR-END");

        bh.consume(_timeLogger);
    }

    @Benchmark
    public void forEachBlock(Blackhole bh) {
        _timeLogger.logTime("FOREACH-START", true);
        for (int i : _VALUES) {
            _timeLogger.logTime("FOREACH-ITERATION:" + i);
        }
        _timeLogger.logTime("FOREACH-END");

        bh.consume(_timeLogger);
    }

    @Benchmark
    public void whileBlock(Blackhole bh) {
        _timeLogger.logTime("WHILE-START", true);
        int j = 0;
        _timeLogger.logTime("WHILE-VAR");
        while (j <= N) {
            _timeLogger.logTime("WHILE-J:" + j);
            j++;
            _timeLogger.logTime("WHILE-J++:" + j);
        }
        _timeLogger.logTime("WHILE-END");

        bh.consume(_timeLogger);
    }

    @Benchmark
    public void doWhileBlock(Blackhole bh) {
        _timeLogger.logTime("DOWHILE-START", true);
        int k = 0;
        _timeLogger.logTime("DOWHILE-VAR");
        do {
            _timeLogger.logTime("DOWHILE-K:" + k);
            k++;
            _timeLogger.logTime("DOWHILE-K++:" + k);
        } while (k <= N);
        _timeLogger.logTime("DOWHILE-END");

        bh.consume(_timeLogger);
    }

    @Benchmark
    public void switchBlock(Blackhole bh) {
        _timeLogger.logTime("SWITCH-START", true);
        switch (N % 2) {
            case 0:
                _timeLogger.logTime("SWITCH-0");
                break;
            case 1:
                _timeLogger.logTime("SWITCH-1");
                break;
            default:
                _timeLogger.logTime("SWITCH-DEFAULT");
        }
        _timeLogger.logTime("SWITCH-END");

        bh.consume(_timeLogger);
    }

    private void getIsWarmup(IterationParams params) {
        boolean isWarmup = params.getType() == IterationType.WARMUP;
        if (!isWarmup) {
            _timeLogger = new TimeLogger(this.getClass().getSimpleName(), N);
        } else {
            System.out.println("Warmup iteration, skipping TimeLogger initialization.");
        }
    }

    private void initVALUES() {
        _VALUES = new int[N];
        for (int i = 0; i < N; i++) {
            _VALUES[i] = i;
        }
    }
}
