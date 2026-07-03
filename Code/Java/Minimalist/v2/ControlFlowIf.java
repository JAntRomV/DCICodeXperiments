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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

public class ControlFlowIf {

    static TimeLogger timeLogger = new TimeLogger();

    public static void main(String[] args) {
        timeLogger.logTime("VAR B:");
        int N = Integer.parseInt(args[0]);
        timeLogger.logTime("VAR F:");

        // IF
        timeLogger.logTime("IF B:");
        if ((N % 2) == 0) {
            timeLogger.logTime("IF I:");
        }
        timeLogger.logTime("IF F:");
        timeLogger.escribirCsv("ControlFlowIf_"+ System.nanoTime() +".csv");
    }
}