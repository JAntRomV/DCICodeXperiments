package com.dci.benchmark.profiler;

/** Representa una fila del CSV de perfilado línea por línea. */
public final class LineRecord {

    final String classId;
    final String iterationId;
    final int seq;
    final String tag;
    final long wallClockNs;
    final long durationNs;
    final String threadName;

    public LineRecord(String classId, String iterationId, int seq, String tag,
                       long wallClockNs, long durationNs, String threadName) {
        this.classId = classId;
        this.iterationId = iterationId;
        this.seq = seq;
        this.tag = tag;
        this.wallClockNs = wallClockNs;
        this.durationNs = durationNs;
        this.threadName = threadName;
    }

    String toCsvRow() {
        return seq + "," + classId + "," + iterationId + "," + tag + ","
                + wallClockNs + "," + durationNs + "," + threadName;
    }
}
