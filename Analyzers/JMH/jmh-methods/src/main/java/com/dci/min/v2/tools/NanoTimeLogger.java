package com.dci.min.v2.tools;

import java.time.LocalDateTime;

public class NanoTimeLogger {

    private long _prevNanos;
    private LocalDateTime _prevFechaHora;
    private int _IDLog;

    public NanoTimeLogger(long prevNanos, LocalDateTime prevFechaHora, int IDLog) {
        this._prevNanos = prevNanos;
        this._prevFechaHora = prevFechaHora;
        this._IDLog = IDLog;
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

    public int getIDLog() {
        return _IDLog;
    }

    public void setIDLog(int IDLog) {
        this._IDLog = IDLog;
    }
}