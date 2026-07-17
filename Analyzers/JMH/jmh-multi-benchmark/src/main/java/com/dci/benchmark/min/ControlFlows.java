package com.dci.benchmark.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Level;
import com.dci.benchmark.profiler.LineProfiler;

@State(Scope.Thread)
public class ControlFlows {

    @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
    public int N;

    private int[] _VALUES;
    private LineProfiler profiler;

    @Setup
    public void setupValues() {
        // Initialize the _VALUES array with values from 0 to N
        _VALUES = new int[N];
        for (int i = 0; i < N; i++) {
            _VALUES[i] = i;
        }

        // iterationId único por invocación para poder cruzar datos si hace falta
        profiler = new LineProfiler("ControlFlows", String.valueOf(System.nanoTime()));
    }

    @Benchmark
    public void exec(Blackhole bh) {
        profiler.mark("BEGIN EXEC");

        // IF
        profiler.mark("IF-START");
        if ((N % 2) == 0) {
            profiler.mark("IF_TRUE");
        }
        profiler.mark("IF-END");

        //IF ELSE
        profiler.mark("IF-ELSE-START");
        if((N % 2) == 0){
            profiler.mark("IF-ELSE_IF");
        }else{
            profiler.mark("IF-ELSE_ELSE");
        }
        profiler.mark("IF-ELSE-END");

        //FOR
        profiler.mark("FOR-START");
        for(int i=0; i<N; i++){
            profiler.mark("FOR-ITERATION:"+i);
        }
        profiler.mark("FOR-END");

        //FOR EACH
        profiler.mark("FOR-EACH-START");
        for(int i : _VALUES){
            profiler.mark("FOR-EACH-ITERATION:"+i);
        }
        profiler.mark("FOR-EACH-END");

        //WHILE
        profiler.mark("WHILE-START");
        int j = 0;
        profiler.mark("WHILE-J-INIT");
        while(j<=N){
            profiler.mark("WHILE-ITERATION-BEFORE:"+j);
            j++;
            profiler.mark("WHILE-ITERATION-AFTER:"+j);
        }
        profiler.mark("WHILE-END");

        //DO WHILE
        profiler.mark("DO-WHILE-START");
        int k = 0;
        profiler.mark("DO-WHILE-K-INIT");
        do{
            profiler.mark("DO-WHILE-ITERATION-BEFORE:"+k);
            k++;                
            profiler.mark("DO-WHILE-ITERATION-AFTER:"+k);
        }while(k<=N);
        profiler.mark("DO-WHILE-END");

        //SWITCH
        profiler.mark("SWITCH-START");
        switch(N%2){
            case 0:
                profiler.mark("SWITCH-CASE-0");
                break;
            case 1:
                profiler.mark("SWITCH-CASE-1");
                break;
            default:
                profiler.mark("SWITCH-DEFAULT");
        }
        profiler.mark("SWITCH-END");

        profiler.mark("END EXEC");
        bh.consume(profiler);
    }

    @TearDown(Level.Invocation)
    public void tearDownInvocation() {
        profiler.flush(); // vuelca al buffer asíncrono, fuera de la sección medida
    }
}
