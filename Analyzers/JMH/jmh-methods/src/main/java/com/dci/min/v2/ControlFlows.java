package com.dci.min.v2;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Setup;

@State(Scope.Benchmark)
public class ControlFlows {

    @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
    public int N;

    private int[] _VALUES;

    private DateTimeFormatter FORMATTER = 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS.AAAA.nnnnnnnnn");
    private List<String[]> LOGS = new ArrayList<>();

    private void logTime(String tag) {
        long nanos = System.nanoTime();  // tiempo de alta resolución
        Instant instant = Instant.now(); // tiempo de reloj real

        LocalDateTime fechaHora = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        LOGS.add(new String[]{String.valueOf(N),
                ControlFlows.class.getSimpleName(),
                tag,
                String.valueOf(nanos),
                fechaHora.format(FORMATTER)});
    }


    @Setup
    public void setupValues() {
        // Initialize the _VALUES array with values from 0 to N
        _VALUES = new int[N];
        for (int i = 0; i <= N; i++) {
            _VALUES[i] = i;
        }

        // Initialize the LOGS list with headers
        LOGS.add(new String[]{"Param","Class", "Tag", "Nanos", "DateTime"});
    }

    @Benchmark
    public void exec(Blackhole bh) {

        // IF
        if ((N % 2) == 0) {
            //CODE
        }

        //IF ELSE
        if((N % 2) == 0){
            //CODE
        }else{
            //CODE
        }

        //FOR
        for(int i=0; i<N; i++){
            //CODE
        }

        //FOR EACH
        for(int i : _VALUES){
            //CODE
        }

        //WHILE
        int j = 0;
        while(j<=N){
            //CODE
            j++;
        }

        //DO WHILE
        int k = 0;
        do{
            //CODE
            k++;                
        }while(k<=N);

        //SWITCH
        switch(N%2){
            case 0:
                //CODE
                break;
            case 1:
                //CODE
                break;
            default:
                //CODE
        }

        bh.consume(k);
    }
}
