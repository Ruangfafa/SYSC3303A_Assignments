import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.logging.*;

public class LoggerService {

    private static class LogModel {

        private static final String THREAD = "\u001B[97;46m";
        private static final String BLANK = "\u001B[0;97;49m";
        private static final String RESET = "\u001b[0;39;49m";
        private static final String GREEN = "\u001B[32m";
        private static final String CYAN = "\u001B[36m";
        private static final String YELLOW = "\u001B[33m";
        private static final String RED = "\u001B[31m";

        final String thread;
        final String time;
        final String level;
        final String[] data;
        final String message;

        public LogModel(String thread, String time, String level, String[] data, String message) {
            this.thread = thread;
            this.time = time;
            this.level = level;
            this.data = data;
            this.message = message;
        }

        public String format() {
            String dataSet = (data == null || data.length == 0)
                    ? ""
                    : " [" + String.join(", ", data) + "]";

            return String.format(
                    "%s[%s]%s[%s]%s%s[%s]%s %s%s",
                    BLANK,
                    time,
                    getColor("THREAD"),
                    thread,
                    RESET,
                    getColor(level),
                    level,
                    dataSet,
                    message,
                    RESET
            );
        }

        private String getColor(String type) {
            return switch (type) {
                case "THREAD" -> THREAD;
                case "INFO" -> GREEN;
                case "WARNING" -> YELLOW;
                case "SEVERE" -> RED;
                case "FINE", "FINER", "FINEST" -> CYAN;
                default -> RESET;
            };
        }
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final ConcurrentLinkedQueue<LogModel> queue = new ConcurrentLinkedQueue<>();

    private final ScheduledExecutorService scheduler;

    private final Logger logger;

    private final Level consoleLevel;

    private final Level fileLevel;

    private final boolean enableFile;

    private FileHandler fileHandler;

    private LocalDate currentDate;

    public LoggerService(long periodMs, boolean enableFile, Level consoleLevel, Level fileLevel) {
        this.consoleLevel = consoleLevel;
        this.fileLevel = fileLevel;
        this.enableFile = enableFile;

        logger = Logger.getLogger("AsyncLogger");
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);

        setupConsoleHandler();

        if (enableFile) {
            try {
                rotateFileIfNeeded();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ThreadFactory factory = r -> {
            Thread t = new Thread(r, "Logger");
            t.setDaemon(true);
            return t;
        };

        scheduler = Executors.newSingleThreadScheduledExecutor(factory);
        scheduler.scheduleAtFixedRate(
                this::flush,
                periodMs,
                periodMs,
                TimeUnit.MILLISECONDS
        );
    }

    private void setupConsoleHandler() {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(consoleLevel);
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage() + "\n";
            }
        });

        logger.addHandler(consoleHandler);
    }

    private void rotateFileIfNeeded() throws IOException {
        LocalDate today = LocalDate.now();

        if (today.equals(currentDate)) return;

        currentDate = today;

        if (fileHandler != null) {
            fileHandler.close();
            logger.removeHandler(fileHandler);
        }

        Path logDir = Paths.get("logs");

        Files.createDirectories(logDir);

        String filename = today + ".log";

        Path logFile = logDir.resolve(filename);

        fileHandler = new FileHandler(logFile.toString(), true);

        fileHandler.setLevel(fileLevel);

        fileHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage() + "\n";
            }
        });

        logger.addHandler(fileHandler);
    }

    public void log(String thread, String level, String[] data, String message) {
        queue.add(new LogModel(
                thread,
                FORMATTER.format(LocalDateTime.now()),
                level,
                data,
                message
        ));
    }

    private void flush() {
        LogModel e;

        try {
            if (enableFile) {
                rotateFileIfNeeded();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        while ((e = queue.poll()) != null) {
            logger.log(
                    toLevel(e.level),
                    e.format()
            );
        }
    }

    private Level toLevel(String level) {
        return switch (level) {
            case "INFO" -> Level.INFO;
            case "WARNING" -> Level.WARNING;
            case "SEVERE" -> Level.SEVERE;
            case "FINE" -> Level.FINE;
            case "FINER" -> Level.FINER;
            case "FINEST" -> Level.FINEST;
            default -> Level.INFO;
        };
    }

    public void shutdown() {
        scheduler.shutdownNow();

        flush();

        if (fileHandler != null) {
            fileHandler.close();
        }
    }
}