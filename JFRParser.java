import jdk.jfr.consumer.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class JFRParser {

    static final String OUTPUT_DIR = "profiling-output";
    static final String CSV_FILE   = "profiling_results.csv";

    public static void main(String[] args) throws Exception {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{
            "className","run","durationMs","cpuSamples",
            "heapUsedMB","gcCount","gcPauseMs","topMethod"
        });

        File outDir = new File(OUTPUT_DIR);
        for (File classDir : Objects.requireNonNull(outDir.listFiles(File::isDirectory))) {
            String className = classDir.getName().replace("_", ".");

            for (File jfr : Objects.requireNonNull(classDir.listFiles(
                    f -> f.getName().endsWith(".jfr")))) {

                String runNum = jfr.getName()
                    .replace("run_","").replace(".jfr","");

                Map<String,Long> methodSamples = new HashMap<>();
                long cpuSamples = 0, gcCount = 0, gcPauseMs = 0;
                long heapUsed = 0, heapReadings = 0;
                long startMs = -1, endMs = -1;

                try (RecordingFile rf = new RecordingFile(jfr.toPath())) {
                    while (rf.hasMoreEvents()) {
                        RecordedEvent ev = rf.readEvent();
                        long ts = ev.getStartTime().toEpochMilli();
                        if (startMs < 0) startMs = ts;
                        endMs = ts;

                        switch (ev.getEventType().getName()) {
                            case "jdk.ExecutionSample" -> {
                                cpuSamples++;
                                RecordedStackTrace st = ev.getStackTrace();
                                if (st != null && !st.getFrames().isEmpty()) {
                                    String m = st.getFrames().get(0)
                                        .getMethod().getType().getName()
                                        + "." + st.getFrames().get(0)
                                        .getMethod().getName();
                                    methodSamples.merge(m, 1L, Long::sum);
                                }
                            }
                            case "jdk.GarbageCollection" -> {
                                gcCount++;
                                gcPauseMs += ev.getDuration().toMillis();
                            }
                            case "jdk.HeapStatistics",
                                 "jdk.GCHeapSummary" -> {
                                if (ev.hasField("heapUsed")) {
                                    heapUsed += ev.getLong("heapUsed") / (1024*1024);
                                    heapReadings++;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error leyendo " + jfr + ": " + e.getMessage());
                    continue;
                }

                long durationMs = (startMs >= 0 && endMs >= startMs)
                    ? endMs - startMs : 0;
                long avgHeapMB  = heapReadings > 0 ? heapUsed / heapReadings : 0;
                String topMethod = methodSamples.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

                rows.add(new String[]{
                    className, runNum,
                    String.valueOf(durationMs),
                    String.valueOf(cpuSamples),
                    String.valueOf(avgHeapMB),
                    String.valueOf(gcCount),
                    String.valueOf(gcPauseMs),
                    topMethod
                });
            }
        }

        // Escribir CSV
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            for (String[] row : rows) {
                pw.println(String.join(",", row));
            }
        }
        System.out.println("CSV generado: " + CSV_FILE + " (" + (rows.size()-1) + " filas)");
    }
}