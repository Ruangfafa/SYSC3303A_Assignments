import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportGenerator {

    private static final DateTimeFormatter TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    // [2026-03-13 21:23:38.234][Agent][FINE] [PLACING_COMPONENTS] Report Pin
    // or
    // [2026-03-13 21:23:39.092][Technician-Frame][INFO] Drone assembled.
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^\\[(.+?)]\\[(.+?)]\\[(.+?)](?: \\[(.*)])? (.*)$"
    );

    private static final Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[[;\\d]*m");

    private static class LogEntry {
        LocalDateTime timestamp;
        String thread;
        String level;
        List<String> data = new ArrayList<>();
        String message;
        String eventCode;
    }

    private static class ThreadStats {
        final String name;

        LocalDateTime firstSeen;
        LocalDateTime doneTime;
        LocalDateTime lastSeen;

        boolean inWait = false;
        LocalDateTime waitStart = null;
        long totalWaitMs = 0L;
        int waitIntervals = 0;

        String currentCycleType = null;
        LocalDateTime cycleStart = null;
        long cycleWaitMs = 0L;
        boolean cycleFinished = false;

        long totalBusyMs = 0L;
        List<Long> responseTimesMs = new ArrayList<>();

        int placedCount = 0;
        int assembledCount = 0;
        int notifyCount = 0;
        Map<String, Integer> rawEventCounts = new LinkedHashMap<>();

        ThreadStats(String name) {
            this.name = name;
        }

        void seenAt(LocalDateTime ts) {
            if (firstSeen == null || ts.isBefore(firstSeen)) firstSeen = ts;
            if (lastSeen == null || ts.isAfter(lastSeen)) lastSeen = ts;
        }

        LocalDateTime endTime() {
            return doneTime != null ? doneTime : lastSeen;
        }

        void incRaw(String code) {
            rawEventCounts.merge(code, 1, Integer::sum);
        }

        void startWait(LocalDateTime ts) {
            if (!inWait) {
                inWait = true;
                waitStart = ts;
            }
        }

        void endWait(LocalDateTime ts) {
            if (inWait && waitStart != null) {
                long ms = Duration.between(waitStart, ts).toMillis();
                if (ms < 0) ms = 0;
                totalWaitMs += ms;
                waitIntervals++;

                if (cycleStart != null && !cycleFinished) {
                    cycleWaitMs += ms;
                }

                inWait = false;
                waitStart = null;
            }
        }

        void startCycle(String type, LocalDateTime ts) {
            if (cycleStart == null) {
                currentCycleType = type;
                cycleStart = ts;
                cycleWaitMs = 0L;
                cycleFinished = false;
            }
        }

        void finishCycle(LocalDateTime ts) {
            if (cycleStart == null || cycleFinished) return;

            long gross = Duration.between(cycleStart, ts).toMillis();
            if (gross < 0) gross = 0;

            long net = gross - cycleWaitMs;
            if (net < 0) net = 0;

            responseTimesMs.add(net);
            totalBusyMs += net;
            cycleFinished = true;

            if ("AGENT".equals(currentCycleType)) {
                placedCount++;
            } else if ("TECHNICIAN".equals(currentCycleType)) {
                assembledCount++;
            }

            currentCycleType = null;
            cycleStart = null;
            cycleWaitMs = 0L;
        }

        long observedSpanMs() {
            if (firstSeen == null || endTime() == null) return 0L;
            long ms = Duration.between(firstSeen, endTime()).toMillis();
            return Math.max(ms, 0L);
        }
    }

    public static void generateReport(String in_file, String out_file) {
        try {
            List<String> rawLines = Files.readAllLines(Paths.get(in_file));
            List<LogEntry> entries = new ArrayList<>();

            for (String raw : rawLines) {
                LogEntry entry = parseLine(raw);
                if (entry != null) {
                    entries.add(entry);
                }
            }

            if (entries.isEmpty()) {
                writeText(out_file, "No parsable log entries found.\n");
                return;
            }

            entries.sort(Comparator.comparing(e -> e.timestamp));

            LinkedHashMap<String, ThreadStats> statsMap = new LinkedHashMap<>();

            for (LogEntry e : entries) {
                ThreadStats stats = statsMap.computeIfAbsent(e.thread, ThreadStats::new);
                stats.seenAt(e.timestamp);

                // A non-WAIT event means the thread has exited its wait interval
                if (stats.inWait && !"WAIT".equals(e.eventCode)) {
                    stats.endWait(e.timestamp);
                }

                if (e.eventCode != null) {
                    stats.incRaw(e.eventCode);

                    switch (e.eventCode) {
                        case "WAIT" -> stats.startWait(e.timestamp);

                        case "PLACING_COMPONENTS" -> stats.startCycle("AGENT", e.timestamp);

                        case "PLACED_COMPONENTS" -> stats.finishCycle(e.timestamp);

                        case "ASSEMBLING" -> stats.startCycle("TECHNICIAN", e.timestamp);

                        case "ASSEMBLED" -> stats.finishCycle(e.timestamp);

                        case "NOTIFY_ALL", "SIGNALLED" -> stats.notifyCount++;

                        case "DONE" -> {
                            if (stats.inWait) {
                                stats.endWait(e.timestamp);
                            }
                            stats.doneTime = e.timestamp;
                        }
                    }
                }
            }

            for (ThreadStats stats : statsMap.values()) {
                if (stats.inWait && stats.lastSeen != null) {
                    stats.endWait(stats.lastSeen);
                }
            }

            LocalDateTime globalStart = null;
            LocalDateTime globalEnd = null;

            for (ThreadStats stats : statsMap.values()) {
                if (stats.firstSeen != null) {
                    if (globalStart == null || stats.firstSeen.isBefore(globalStart)) {
                        globalStart = stats.firstSeen;
                    }
                }
                if (stats.endTime() != null) {
                    if (globalEnd == null || stats.endTime().isAfter(globalEnd)) {
                        globalEnd = stats.endTime();
                    }
                }
            }

            if (globalStart == null || globalEnd == null) {
                writeText(out_file, "Could not determine report time range.\n");
                return;
            }

            int totalDrones = 0;

            long totalExecMs = Math.max(Duration.between(globalStart, globalEnd).toMillis(), 0L);
            double throughput = totalExecMs > 0
                    ? totalDrones / (totalExecMs / 1000.0)
                    : 0.0;

            StringBuilder sb = new StringBuilder();

            sb.append("Drone Assembly Line Report").append(System.lineSeparator());
            sb.append("============================================================").append(System.lineSeparator());
            sb.append(String.format("Start time          : %s%n", TS_FORMAT.format(globalStart)));
            sb.append(String.format("End time            : %s%n", TS_FORMAT.format(globalEnd)));
            sb.append(String.format("Total execution time: %s%n", formatDuration(totalExecMs)));
            sb.append(String.format("Total drones        : %d%n", totalDrones));
            sb.append(String.format("Throughput          : %.4f drones/second%n", throughput));

            for (ThreadStats stats : statsMap.values()) {
                sb.append("------------------------------------------------------------").append(System.lineSeparator());
                sb.append(stats.name).append(System.lineSeparator());

                sb.append(String.format("  ASSEMBLED count   : %d%n", stats.assembledCount));
                sb.append(String.format("  PLACED count      : %d%n", stats.placedCount));
                if (stats.responseTimesMs.isEmpty()) {
                    sb.append("  Average Response time      : N/A").append(System.lineSeparator());
                    sb.append("  Minimum Response time      : N/A").append(System.lineSeparator());
                    sb.append("  Maximum Response time      : N/A").append(System.lineSeparator());
                } else {
                    long min = Long.MAX_VALUE;
                    long max = Long.MIN_VALUE;
                    long sum = 0L;

                    for (long ms : stats.responseTimesMs) {
                        min = Math.min(min, ms);
                        max = Math.max(max, ms);
                        sum += ms;
                    }

                    double avg = sum / (double) stats.responseTimesMs.size();
                    sb.append(String.format("  Average Response time      : %.2f ms%n", avg));
                    sb.append(String.format("  Minimum Response time      : %d ms%n", min));
                    sb.append(String.format("  Maximum Response time      : %d ms%n", max));
                }

                long busyMs = stats.totalBusyMs;
                long waitMs = stats.totalWaitMs;
                long observedMs = stats.observedSpanMs();
                long busyPlusWait = busyMs + waitMs;

                double utilObserved = observedMs > 0 ? (busyMs * 100.0 / observedMs) : 0.0;
                double busyVsBusyWait = busyPlusWait > 0 ? (busyMs * 100.0 / busyPlusWait) : 0.0;

                sb.append(String.format("  Busy time         : %s (%d ms)%n", formatDuration(busyMs), busyMs));
                sb.append(String.format("  Wait time         : %s (%d ms)%n", formatDuration(waitMs), waitMs));
                sb.append(String.format("  Observed span     : %s (%d ms)%n", formatDuration(observedMs), observedMs));
                sb.append(String.format("  Utilization       : %.2f%%%n", utilObserved));
                sb.append(String.format("  Busy/(Busy+Wait)  : %.2f%%%n", busyVsBusyWait));
            }

            writeText(out_file, sb.toString());

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate report from " + in_file, e);
        }
    }

    private static LogEntry parseLine(String rawLine) {
        if (rawLine == null || rawLine.isBlank()) return null;

        String line = ANSI_PATTERN.matcher(rawLine).replaceAll("").trim();
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (!matcher.matches()) return null;

        LogEntry entry = new LogEntry();
        entry.timestamp = LocalDateTime.parse(matcher.group(1).trim(), TS_FORMAT);
        entry.thread = matcher.group(2).trim();
        entry.level = matcher.group(3).trim();

        String dataGroup = matcher.group(4);
        if (dataGroup != null && !dataGroup.isBlank()) {
            String[] tokens = dataGroup.split(",\\s*");
            entry.data.addAll(Arrays.asList(tokens));
        }

        entry.message = matcher.group(5).trim();

        if ("Report Pin".equals(entry.message) && !entry.data.isEmpty()) {
            entry.eventCode = entry.data.getFirst().trim();
        }

        return entry;
    }

    private static void writeText(String outFile, String text) throws IOException {
        Path path = Paths.get(outFile);
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(path, text);
    }

    private static String formatDuration(long ms) {
        if (ms < 0) ms = 0;
        long hours = ms / 3_600_000;
        long minutes = (ms % 3_600_000) / 60_000;
        long seconds = (ms % 60_000) / 1_000;
        long millis = ms % 1_000;
        return String.format("%02dh:%02dm:%02ds.%03d", hours, minutes, seconds, millis);
    }
}