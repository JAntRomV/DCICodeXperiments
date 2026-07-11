package com.dci.benchmark.profiler;

/**
 * Genera una marca de "hora de pared" (epoch) con resolución de nanosegundos.
 *
 * NOTA IMPORTANTE:
 * Instant.now() / System.currentTimeMillis() en la JVM suelen tener resolución
 * real de microsegundos o milisegundos (depende del SO), NUNCA nanosegundos reales.
 * Esta clase ancla el reloj de pared (millis) a System.nanoTime() (que sí tiene
 * resolución de nanosegundos, aunque no es una "fecha") para poder reportar
 * timestamps con precisión de nanosegundo de forma consistente y monotónica.
 *
 * Es decir: el valor es preciso en resolución, pero su exactitud respecto al
 * reloj de pared real está limitada por la resolución real del SO en el
 * instante del anclaje inicial. Para medir duraciones (que es lo que más
 * importa en benchmarking) usa siempre System.nanoTime() directamente,
 * como hace LineProfiler.
 */
public final class NanoClock {

    private static final long EPOCH_ANCHOR_MILLIS = System.currentTimeMillis();
    private static final long NANO_ANCHOR = System.nanoTime();

    private NanoClock() {
    }

    /** Devuelve una estimación de epoch en nanosegundos (no una hora "real" de nanosegundo). */
    public static long epochNanos() {
        long deltaNanos = System.nanoTime() - NANO_ANCHOR;
        return EPOCH_ANCHOR_MILLIS * 1_000_000L + deltaNanos;
    }
}
