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
        _timeLogger.toCSV("ControlFlows_" + N + ".csv");
    }

    @Benchmark
    public void exec(Blackhole bh) {

        // IF
        _timeLogger.logTime("IF-S:");
        if ((N % 2) == 0) {
            _timeLogger.logTime("IF-I:");
        }
        _timeLogger.logTime("IF-F:");

        // //IF ELSE
        // if((N % 2) == 0){
        //     //CODE
        // }else{
        //     //CODE
        // }

        // //FOR
        // for(int i=0; i<N; i++){
        //     //CODE
        // }

        // //FOR EACH
        // for(int i : _VALUES){
        //     //CODE
        // }

        // //WHILE
        // int j = 0;
        // while(j<=N){
        //     //CODE
        //     j++;
        // }

        // //DO WHILE
        // int k = 0;
        // do{
        //     //CODE
        //     k++;                
        // }while(k<=N);

        // //SWITCH
        // switch(N%2){
        //     case 0:
        //         //CODE
        //         break;
        //     case 1:
        //         //CODE
        //         break;
        //     default:
        //         //CODE
        // }

        bh.consume(_timeLogger);
    }
}
