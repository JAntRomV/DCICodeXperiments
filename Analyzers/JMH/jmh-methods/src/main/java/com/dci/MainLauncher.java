package com.dci;

import com.dci.min.v2.tools.DirFileTools;
import com.dci.min.v2.tools.JMHRunner;
import java.util.Map;

public class MainLauncher {
    private static final String[] MIN_CLASSES = {
            "com.dci.min.v2.ControlFlows",
            "com.dci.min.v2.ControlFlowsSeparated",
            "com.dci.min.v2.IfFlow",
            "com.dci.min.v2.IfElseFlow",
            "com.dci.min.v2.ForFlow",
            "com.dci.min.v2.ForEachFlow",
            "com.dci.min.v2.WhileFlow",
            "com.dci.min.v2.DoWhileFlow",
            "com.dci.min.v2.SwitchFlow"
    };

    public static void main(String[] args) throws Exception {

        Map<String, String> params = DirFileTools.getParams(args);

        int Iterations = Integer.parseInt(params.getOrDefault("I", "100"));         //Default 100 iterations
        int WarmupIterations = Integer.parseInt(params.getOrDefault("WI", "10"));   //Default 10 warmup iterations
        int Forks = Integer.parseInt(params.getOrDefault("F", "4"));                //Default 4 forks
        int MinHeap = Integer.parseInt(params.getOrDefault("MINH", "4096"));        //Min heap en MB
        int MaxHeap = Integer.parseInt(params.getOrDefault("MAXH", "4096"));        //Max heap en MB
        int measurementIterations = Iterations / Forks;                             //Calculate measurement iterations per fork

        for (String className : MIN_CLASSES) {
            System.out.println("Running benchmark for: " + className);
            JMHRunner.runBenchmark(className, Iterations, measurementIterations, WarmupIterations, Forks, MinHeap, MaxHeap);
        }
    }
}
