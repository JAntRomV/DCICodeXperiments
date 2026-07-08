package com.dci.min.v2;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import com.dci.min.v2.tools.TimeLogger;

@State(Scope.Benchmark)
public class SwitchFlow {

    @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
    public int N;

    private int[] _VALUES;

    private TimeLogger _timeLogger;

    @Setup
    public void setupValues() {
        _VALUES = new int[N];
        for (int i = 0; i < N; i++) {
            _VALUES[i] = i;
        }

        _timeLogger = new TimeLogger(this.getClass().getSimpleName(), N);
    }

    @TearDown
    public void tearDown() {
        String resultsDirectory = System.getProperty("results.directory", "");
        String resultsCSV = resultsDirectory + "/" + this.getClass().getSimpleName() + "_" + N + ".csv";
        _timeLogger.toCSV(resultsCSV);
    }

    @Benchmark
    public void exec(Blackhole bh) {
        _timeLogger.logTime("SWITCH-S");
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
        _timeLogger.logTime("SWITCH-F");

        bh.consume(_timeLogger);
    }
}
