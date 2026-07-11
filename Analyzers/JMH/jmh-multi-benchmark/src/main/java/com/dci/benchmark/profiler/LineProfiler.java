package com.dci.benchmark.profiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Módulo interno de perfilado línea por línea, pensado para usarse DENTRO
 * de los métodos anotados con @Benchmark.
 *
 * Uso típico dentro de una clase de benchmark:
 *
 *   profiler.mark("inicio");
 *   ... código a medir ...
 *   profiler.mark("paso_1");
 *   ... más código ...
 *   profiler.mark("paso_2");
 *
 * Cada llamada a mark(tag) registra:
 *   - el "tag" (etiqueta que identifica el tipo de línea/paso)
 *   - la duración en nanosegundos desde la marca anterior (o desde la
 *     creación del profiler si es la primera)
 *   - una estimación de hora de pared con resolución de nanosegundos
 *
 * IMPORTANTE SOBRE PRECISIÓN DE LA MEDICIÓN:
 * mark() solo hace operaciones en memoria (ArrayList.add), por lo que su
 * overhead es mínimo y consistente, adecuado para no distorsionar demasiado
 * el código medido. El volcado a disco NUNCA ocurre aquí: ocurre en
 * flush(), que se debe llamar FUERA de la sección medida por JMH
 * (típicamente en un método @TearDown), delegando la escritura real a
 * LineCsvWriter, que lo hace de forma asíncrona.
 */
public final class LineProfiler {

    private final String classId;
    private final String iterationId;
    private final List<String> tags = new ArrayList<>();
    private final List<long[]> ticks = new ArrayList<>(); // [nanoAbsoluto, duracionDesdeAnterior]
    private long lastNanoTime;

    public LineProfiler(String classId, String iterationId) {
        this.classId = classId;
        this.iterationId = iterationId;
        this.lastNanoTime = System.nanoTime();
    }

    /** Marca el paso de una línea/bloque de código, identificado por su tag. */
    public void mark(String tag) {
        long now = System.nanoTime();
        long duration = now - lastNanoTime;
        tags.add(tag);
        ticks.add(new long[]{now, duration});
        lastNanoTime = now;
    }

    /**
     * Envía todas las marcas acumuladas al escritor asíncrono de CSV y
     * limpia el buffer interno. Debe llamarse fuera de la ventana medida
     * por JMH (p. ej. en @TearDown(Level.Invocation) o Level.Iteration).
     */
    public void flush() {
        for (int i = 0; i < tags.size(); i++) {
            long[] t = ticks.get(i);
            long absoluteNano = t[0];
            long durationNs = t[1];
            long wallClockNs = NanoClock.epochNanos() - (System.nanoTime() - absoluteNano);
            LineCsvWriter.enqueue(new LineRecord(
                    classId, iterationId, i + 1, tags.get(i), wallClockNs, durationNs,
                    Thread.currentThread().getName()));
        }
        tags.clear();
        ticks.clear();
    }
}
