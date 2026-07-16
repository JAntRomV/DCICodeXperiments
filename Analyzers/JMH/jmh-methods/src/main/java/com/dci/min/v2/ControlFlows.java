package com.dci.min.v2;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.runner.IterationType;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Setup;
import com.dci.min.v2.tools.TimeLogger;
import org.openjdk.jmh.annotations.Level;

@State(Scope.Benchmark)
public class ControlFlows {

    @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
    public int N;

    private int[] _VALUES;
    private TimeLogger _timeLogger = new TimeLogger();
    private String _resultsDirectory;

    @Setup(Level.Iteration)
    public void setupValues(IterationParams params) {
        // Initialize the _VALUES array with values from 0 to N
        _VALUES = new int[N];
        for (int i = 0; i < N; i++) {
            _VALUES[i] = i;
        }
        
        _resultsDirectory = System.getProperty("results.directory", "");

        //Set values logger for each benchmark execution
        boolean isWarmup = params.getType() == IterationType.WARMUP;
        if(!isWarmup){
            _timeLogger = new TimeLogger(this.getClass().getSimpleName(), N);
        }
        else{
            System.out.println("Warmup iteration, skipping TimeLogger initialization.");
        }
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        String resultsCSV = _resultsDirectory + "/" + this.getClass().getSimpleName() + "_" + N + ".csv";
        _timeLogger.toCSV(resultsCSV);
    }

    @Benchmark
    public void exec(Blackhole bh) {
        // IF
        _timeLogger.logTime("IF-START", true);
        if ((N % 2) == 0) {
            _timeLogger.logTime("IF-TRUE");
        }
        _timeLogger.logTime("IF-END");

        //IF ELSE
        _timeLogger.logTime("IFELSE-START");
        if((N % 2) == 0){
            _timeLogger.logTime("IFELSE-TRUE");
        }else{
            _timeLogger.logTime("IFELSE-FALSE");
        }
        _timeLogger.logTime("IFELSE-END");

        //FOR
        _timeLogger.logTime("FOR-START");
        for(int i=0; i<N; i++){
            _timeLogger.logTime("FOR-ITERATION:" + i);
        }
        _timeLogger.logTime("FOR-END");

        //FOR EACH
        _timeLogger.logTime("FOREACH-START");
        for(int i : _VALUES){
             _timeLogger.logTime("FOREACH-ITERATION:" + i);
        }
        _timeLogger.logTime("FOREACH-END");

        //WHILE
        _timeLogger.logTime("WHILE-START");
        int j = 0;
        _timeLogger.logTime("WHILE-VAR");
        while(j<=N){
            _timeLogger.logTime("WHILE-J:"+j);
            j++;
            _timeLogger.logTime("WHILE-J++:"+j);
        }
        _timeLogger.logTime("WHILE-END");

        //DO WHILE
        _timeLogger.logTime("DOWHILE-START");
        int k = 0;
        _timeLogger.logTime("DOWHILE-VAR");
        do{
            _timeLogger.logTime("DOWHILE-K:"+k);
            k++;                
            _timeLogger.logTime("DOWHILE-K++:"+k);
        }while(k<=N);
        _timeLogger.logTime("DOWHILE-END");

        //SWITCH
        _timeLogger.logTime("SWITCH-START");
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
        _timeLogger.logTime("SWITCH-END");

        bh.consume(_timeLogger);
    }
}
