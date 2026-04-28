package Code.Java.Abacus.v1.Rare;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;

/**
 * ProfilingAgent
 * ──────────────
 * Agente Java (-javaagent) que captura métricas JVM antes y después de la
 * ejecución de la clase objetivo y las imprime como una línea CSV en stdout.
 *
 * Columnas generadas (sin timestamp ni exit_code, que añade el script bash):
 *   class_name, run_number,
 *   execution_time_ms,
 *   heap_used_before_mb, heap_used_after_mb, heap_max_mb,
 *   gc_count_before, gc_count_after,
 *   gc_time_ms_before, gc_time_ms_after,
 *   cpu_load_percent,
 *   threads_live
 *
 * Compilación y empaquetado los realiza run_profiler.sh automáticamente.
 */
public class ProfilingAgent {

    // Propiedades del sistema inyectadas por run_profiler.sh
    private static final String PROP_CLASS = "profiling.class";
    private static final String PROP_RUN   = "profiling.run";

    // -----------------------------------------------------------------------
    // premain: punto de entrada del agente (-javaagent)
    // -----------------------------------------------------------------------
    public static void premain(String agentArgs, Instrumentation inst) {
        String targetClass = System.getProperty(PROP_CLASS, "Unknown");
        String runNumber   = System.getProperty(PROP_RUN,   "0");

        // Snapshot ANTES de que se ejecute el main de la clase objetivo
        Snapshot before = Snapshot.capture();

        // Hook de cierre: se dispara cuando el proceso termina (tras el main)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Snapshot after = Snapshot.capture();
            long execMs = after.uptimeMs - before.uptimeMs;

            // Formatear como línea CSV (sin salto de línea hasta el final)
            System.out.printf(
                "%s,%s,%d,%.2f,%.2f,%.2f,%d,%d,%d,%d,%.2f,%d%n",
                targetClass,
                runNumber,
                execMs,
                before.heapUsedMb,
                after.heapUsedMb,
                after.heapMaxMb,
                before.gcCount,
                after.gcCount,
                before.gcTimeMs,
                after.gcTimeMs,
                after.cpuLoad * 100.0,
                after.threadsLive
            );
        }, "ProfilingAgentShutdown"));
    }

    // -----------------------------------------------------------------------
    // agentmain: punto de entrada para attach dinámico (VisualVM / jattach)
    // -----------------------------------------------------------------------
    public static void agentmain(String agentArgs, Instrumentation inst) {
        premain(agentArgs, inst);
    }

    // -----------------------------------------------------------------------
    // Clase interna: instantánea de métricas JVM
    // -----------------------------------------------------------------------
    private static class Snapshot {
        final long   uptimeMs;
        final double heapUsedMb;
        final double heapMaxMb;
        final long   gcCount;
        final long   gcTimeMs;
        final double cpuLoad;
        final int    threadsLive;

        private Snapshot(long uptimeMs, double heapUsedMb, double heapMaxMb,
                         long gcCount, long gcTimeMs, double cpuLoad, int threadsLive) {
            this.uptimeMs    = uptimeMs;
            this.heapUsedMb  = heapUsedMb;
            this.heapMaxMb   = heapMaxMb;
            this.gcCount     = gcCount;
            this.gcTimeMs    = gcTimeMs;
            this.cpuLoad     = cpuLoad;
            this.threadsLive = threadsLive;
        }

        static Snapshot capture() {
            // Uptime de la JVM
            long uptime = ManagementFactory.getRuntimeMXBean().getUptime();

            // Heap
            MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heap     = memBean.getHeapMemoryUsage();
            double usedMb = heap.getUsed()  / (1024.0 * 1024.0);
            double maxMb  = heap.getMax()   / (1024.0 * 1024.0);

            // GC acumulado
            List<GarbageCollectorMXBean> gcBeans =
                ManagementFactory.getGarbageCollectorMXBeans();
            long totalGcCount = 0;
            long totalGcTime  = 0;
            for (GarbageCollectorMXBean gc : gcBeans) {
                long c = gc.getCollectionCount();
                long t = gc.getCollectionTime();
                if (c > 0) totalGcCount += c;
                if (t > 0) totalGcTime  += t;
            }

            // CPU del proceso (requiere com.sun.management.OperatingSystemMXBean)
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            double cpuLoad = -1.0;
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                cpuLoad = ((com.sun.management.OperatingSystemMXBean) osBean)
                              .getProcessCpuLoad();
            }

            // Threads vivos
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            int threads = threadBean.getThreadCount();

            return new Snapshot(uptime, usedMb, maxMb, totalGcCount, totalGcTime, cpuLoad, threads);
        }
    }
}
