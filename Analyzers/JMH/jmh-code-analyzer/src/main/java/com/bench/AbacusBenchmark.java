package com.bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(value = 3)                          // 3 JVMs distintas = menos sesgo
@Warmup(iterations = 15, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 334, time = 500, timeUnit = TimeUnit.MILLISECONDS)
public class AbacusBenchmark {

    @Param({""}) // Se llenará dinámicamente si es necesario, o vía iteración manual
    public String className;

    @Benchmark
    public void testMethod(Blackhole bh) throws Exception {
        // Aquí instanciamos la clase dinámicamente
        // Asumiendo que quieres medir el costo de creación/uso básico
        Class<?> clazz = Class.forName(className);
        Object instance = clazz.getDeclaredConstructor().newInstance();
        bh.consume(instance);
    }
}