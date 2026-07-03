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

public class NanoTimeLogger {

    private long _prevNanos;
    private LocalDateTime _prevFechaHora;

    public NanoTimeLogger(long prevNanos, LocalDateTime prevFechaHora) {
        this._prevNanos = prevNanos;
        this._prevFechaHora = prevFechaHora;
    }

    public long getPrevNanos() {
        return _prevNanos;
    }

    public void setPrevNanos(long prevNanos) {
        this._prevNanos = prevNanos;
    }

    public LocalDateTime getPrevFechaHora() {
        return _prevFechaHora;
    }

    public void setPrevFechaHora(LocalDateTime prevFechaHora) {
        this._prevFechaHora = prevFechaHora;
    }
}