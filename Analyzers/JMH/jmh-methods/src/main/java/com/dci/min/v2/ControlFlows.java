package com.dci.min.v2;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Setup;
import com.dci.min.v2.tools.TimeLogger;

@State(Scope.Benchmark)
public class ControlFlows {

    @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
    public int N;

    private int[] _VALUES;

    private TimeLogger _timeLogger;

    @Setup
    public void setupValues() {
        // Initialize the _VALUES array with values from 0 to N
        _VALUES = new int[N];
        for (int i = 0; i < N; i++) {
            _VALUES[i] = i;
        }

        //Set values logger for each benchmark execution
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

        // IF
        _timeLogger.logTime("IF-S");
        if ((N % 2) == 0) {
            _timeLogger.logTime("IF-I");
        }
        _timeLogger.logTime("IF-F");

        //IF ELSE
        _timeLogger.logTime("IFELSE-S");
        if((N % 2) == 0){
            _timeLogger.logTime("IFELSE-T");
        }else{
            _timeLogger.logTime("IFELSE-F");
        }
        _timeLogger.logTime("IFELSE-F");

        //FOR
        _timeLogger.logTime("FOR-S");
        for(int i=0; i<N; i++){
            _timeLogger.logTime("FOR-I"+i);
        }
        _timeLogger.logTime("FOR-F");

        //FOR EACH
        _timeLogger.logTime("FOREACH-S");
        for(int i : _VALUES){
             _timeLogger.logTime("FOREACH-I:"+i);
        }
        _timeLogger.logTime("FOREACH-F");

        //WHILE
        _timeLogger.logTime("WHILE-S");
        int j = 0;
        _timeLogger.logTime("WHILE-V");
        while(j<=N){
            _timeLogger.logTime("WHILE-J:"+j);
            j++;
            _timeLogger.logTime("WHILE-J++:"+j);
        }
        _timeLogger.logTime("WHILE-F");

        //DO WHILE
        _timeLogger.logTime("DOWHILE-S");
        int k = 0;
        _timeLogger.logTime("DOWHILE-V");
        do{
            _timeLogger.logTime("DOWHILE-K:"+k);
            k++;                
            _timeLogger.logTime("DOWHILE-K++:"+k);
        }while(k<=N);
        _timeLogger.logTime("DOWHILE-F");

        //SWITCH
        _timeLogger.logTime("SWITCH-S");
        switch(N%2){
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
