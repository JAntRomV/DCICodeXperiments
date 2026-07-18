package com.dci;

import com.dci.min.v2.tools.DirFileTools;
import com.dci.min.v2.tools.JMHRunner;
import java.util.Map;

public class MainLauncher {
    private static final String[] MIN_CLASSES = {
            "com.dci.min.v2.timer.ControlFlows",
            "com.dci.min.v2.timer.SeparatedControlFlows",
            "com.dci.min.v2.timer.IfFlow",
            "com.dci.min.v2.timer.IfElseFlow",
            "com.dci.min.v2.timer.ForFlow",
            "com.dci.min.v2.timer.ForEachFlow",
            "com.dci.min.v2.timer.WhileFlow",
            "com.dci.min.v2.timer.DoWhileFlow",
            "com.dci.min.v2.timer.SwitchFlow"
    };

    private static final String[] NOTIMER_CLASSES = {
            "com.dci.min.v2.notimer.ControlFlows",
            "com.dci.min.v2.notimer.SeparatedControlFlows",
            // "com.dci.min.v2.notimer.IfFlow",
            // "com.dci.min.v2.notimer.IfElseFlow",
            // "com.dci.min.v2.notimer.ForFlow",
            // "com.dci.min.v2.notimer.ForEachFlow",
            // "com.dci.min.v2.notimer.WhileFlow",
            // "com.dci.min.v2.notimer.DoWhileFlow",
            // "com.dci.min.v2.notimer.SwitchFlow"
    };

    public static void main(String[] args) throws Exception {

        Map<String, String> params = DirFileTools.getParams(args);

        int Iterations = Integer.parseInt(params.getOrDefault("I", "1"));           //Default 100 iterations
        int WarmupIterations = Integer.parseInt(params.getOrDefault("WI", "1"));    //Default 10 warmup iterations
        int Forks = Integer.parseInt(params.getOrDefault("F", "1"));                //Default 4 forks
        int MinHeap = Integer.parseInt(params.getOrDefault("MINH", "4096"));        //Min heap en MB
        int MaxHeap = Integer.parseInt(params.getOrDefault("MAXH", "4096"));        //Max heap en MB
        String JMHMode = params.getOrDefault("JMHMODE", "SAMT");                    //Default JMH mode enabled
        int nThreads = Integer.parseInt(params.getOrDefault("THREADS", "1"));       //Default 1 thread
        int measurementIterations = Iterations / Forks;                             //Calculate measurement iterations per fork

        JMHRunner.runBenchmark(Iterations, 
            measurementIterations, 
            WarmupIterations, 
            Forks, 
            MinHeap, 
            MaxHeap, 
            NOTIMER_CLASSES, 
            JMHMode, 
            nThreads);
    }
}
