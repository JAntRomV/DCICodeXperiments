package com.dci.benchmark.profiler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Escritor singleton en segundo plano para el CSV línea-por-línea.
 *
 * DISEÑO CLAVE PARA NO DISTORSIONAR EL BENCHMARK:
 * LineProfiler.mark(tag) solo hace operaciones en memoria (muy baratas).
 * El volcado real a disco ocurre en un hilo demonio independiente que
 * drena una cola concurrente cada cierto intervalo. Así el I/O de disco
 * (lento e impredecible) nunca ocurre dentro de la ventana medida por JMH,
 * siempre que flush() se invoque fuera de @Benchmark (p. ej. en @TearDown).
 */
public final class LineCsvWriter {

    private static final Path CSV_PATH = Paths.get("results", "line_profile.csv");
    private static final ConcurrentLinkedQueue<LineRecord> QUEUE = new ConcurrentLinkedQueue<>();
    private static volatile boolean started = false;
    private static Thread writerThread;

    private LineCsvWriter() {
    }

    public static void enqueue(LineRecord record) {
        ensureStarted();
        QUEUE.add(record);
    }

    private static synchronized void ensureStarted() {
        if (started) return;
        try {
            Files.createDirectories(CSV_PATH.getParent());
            boolean exists = Files.exists(CSV_PATH);
            if (!exists) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_PATH.toFile(), true))) {
                    bw.write("seq,class_id,iteration_id,tag,wall_clock_ns,duration_ns,thread");
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar " + CSV_PATH, e);
        }

        writerThread = new Thread(LineCsvWriter::drainLoop, "line-csv-writer");
        writerThread.setDaemon(true);
        writerThread.start();

        // Asegura que lo que quede en cola se escriba al terminar la JVM (p. ej. tras el último fork de JMH)
        Runtime.getRuntime().addShutdownHook(new Thread(LineCsvWriter::flushRemaining, "line-csv-shutdown"));

        started = true;
    }

    private static void drainLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            flushRemaining();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void flushRemaining() {
        if (QUEUE.isEmpty()) return;
        List<LineRecord> batch = new ArrayList<>();
        LineRecord r;
        while ((r = QUEUE.poll()) != null) {
            batch.add(r);
        }
        if (batch.isEmpty()) return;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_PATH.toFile(), true))) {
            for (LineRecord rec : batch) {
                bw.write(rec.toCsvRow());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error escribiendo lote de perfilado: " + e.getMessage());
        }
    }
}
