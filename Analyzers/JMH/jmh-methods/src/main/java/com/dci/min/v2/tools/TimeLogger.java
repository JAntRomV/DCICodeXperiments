package com.dci.min.v2.tools;

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

public class TimeLogger {
    
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS.AAAA.nnnnnnnnn");
    private final List<String[]> LOGS = new ArrayList<>();
    private NanoTimeLogger _prevTimes;
    private String[] _header = {"IDLog","Iteracion","Clase", "ParamN" ,"Etiqueta", "TiempoNanos", "FechaHora", "DuracionNanos","DuracionNanosTime"};
    private int _iterationCount;

    private String className;
    private int paramN;

    public TimeLogger() {}

    public TimeLogger(String className, int paramN) {
        this.className = className;
        this.paramN = paramN;
        this._prevTimes = new NanoTimeLogger(System.nanoTime(), LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),0);
        LOGS.add(_header);
    }

    public void logTime(String etiqueta) {
        logTime(etiqueta, false);
    }

    public void logTime(String etiqueta, boolean isNewIteration) {
        if(this._prevTimes != null){
            if(isNewIteration){
                this._iterationCount++;
            }
            int IdLOG = this._prevTimes.getIDLog() + 1;
            long nanos = System.nanoTime();  // tiempo de alta resolución
            Instant instant = Instant.now(); // tiempo de reloj real
            LocalDateTime fechaHora = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

            long durationNanos = nanos - _prevTimes.getPrevNanos();
            Duration duration = Duration.between(_prevTimes.getPrevFechaHora(), fechaHora);
            long durationNanosTime = duration.toNanos();

            LOGS.add(new String[]{
                    String.valueOf(IdLOG),
                    String.valueOf(this._iterationCount),
                    this.className,
                    String.valueOf(this.paramN),
                    etiqueta,
                    String.valueOf(nanos),
                    fechaHora.format(FORMATTER),
                    String.valueOf(durationNanos),
                    String.valueOf(durationNanosTime)});

            _prevTimes.setIDLog(IdLOG);
            _prevTimes.setPrevNanos(nanos);
            _prevTimes.setPrevFechaHora(fechaHora);
        }
    }

    public void toCSV(String rutaArchivo) {
        if(!LOGS.isEmpty()) {
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
}
