package com.dci.benchmark.runner;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import com.dci.benchmark.profiler.SummaryGenerator;
import com.dci.benchmark.tools.DirFileTools;
import com.dci.benchmark.tools.JMHRunner;

import java.util.List;
import java.util.Map;

/**
 * Punto de entrada único que ejecuta JMH sobre varias clases de benchmark,
 * con 100 iteraciones de medición por clase, y vuelca los resultados a CSV.
 *
 * Cómo agregar más clases:
 *   Simplemente añade el nombre completamente calificado (o un patrón regex
 *   que JMH pueda resolver con @Fork / include) a TARGET_CLASSES.
 *
 * Resultados generados:
 *   - results/jmh_results.csv          -> resultados oficiales de JMH (throughput,
 *                                          avgt, sampletime, etc. según @BenchmarkMode)
 *   - results/line_profile.csv         -> tiempos línea por línea con tag, generado
 *                                          por LineProfiler dentro de cada clase
 *   - results/line_profile_summary.csv -> promedios y percentiles por (clase, tag)
 */
public final class MultiClassBenchmarkRunner {

    // Lista de clases de benchmark a ejecutar. Deben tener métodos @Benchmark.
    private static final List<String> TARGET_CLASSES = List.of(
            "com.dci.benchmark.min.ControlFlows",
            "com.dci.benchmark.min.SeparatedControlFlows"
    );

    public static void main(String[] args) throws Exception {
        Map<String, String> params = DirFileTools.getParams(args);

        int Iterations = Integer.parseInt(params.getOrDefault("I", "1"));          //Default 1 iterations
        int WarmupIterations = Integer.parseInt(params.getOrDefault("WI", "1"));   //Default 1 warmup iterations
        int Forks = Integer.parseInt(params.getOrDefault("F", "1"));                //Default 1 forks
        int MinHeap = Integer.parseInt(params.getOrDefault("MINH", "4096"));        //Min heap en MB
        int MaxHeap = Integer.parseInt(params.getOrDefault("MAXH", "4096"));        //Max heap en MB
        int measurementIterations = Iterations / Forks;                                               //Calculate measurement iterations per fork
    
        JMHRunner.runBenchmark(TARGET_CLASSES, Iterations, measurementIterations, WarmupIterations, Forks, MinHeap, MaxHeap);
    }
}
