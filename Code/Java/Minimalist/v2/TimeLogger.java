package Code.Java.Minimalist.v2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.IntStream;

public class TimeLogger {
    
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS.AAAA.nnnnnnnnn");
    private final List<String[]> LOGS = new ArrayList<>();
    private NanoTimeLogger _prevTimes = new NanoTimeLogger(System.nanoTime(), LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

    public TimeLogger() {
        LOGS.add(new String[]{"Clase", "Etiqueta", "TiempoNanos", "FechaHora", "DuracionNanos","DuracionNanosTime"});
    }

    public void logTime(String etiqueta) {
        long nanos = System.nanoTime();  // tiempo de alta resolución
        Instant instant = Instant.now(); // tiempo de reloj real
        LocalDateTime fechaHora = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        long durationNanos = nanos - _prevTimes.getPrevNanos();
        Duration duration = Duration.between(_prevTimes.getPrevFechaHora(), fechaHora);
        long durationNanosTime = duration.toNanos();

        LOGS.add(new String[]{ControlFlowIf.class.getSimpleName(),
                etiqueta,
                String.valueOf(nanos),
                fechaHora.format(FORMATTER),
                String.valueOf(durationNanos),
                String.valueOf(durationNanosTime)});

        _prevTimes.setPrevNanos(nanos);
        _prevTimes.setPrevFechaHora(fechaHora);
    }

    public void escribirCsv(String rutaArchivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            for (String[] fila : LOGS) {
                writer.write(String.join(",", fila));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
