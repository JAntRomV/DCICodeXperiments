package com.dci.min;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class SwitchStatement {

    @Param({"1","2","0"})
    public int _OPTION;

    @Benchmark
    public void exec(Blackhole bh) {
        int option = _OPTION;
        switch (option) {
            case 1:
                option = 1;
                break;
            case 2:
                option = 2;
                break;
            default:
                option = 0;
                break;
        }
        bh.consume(option);
    }
}
