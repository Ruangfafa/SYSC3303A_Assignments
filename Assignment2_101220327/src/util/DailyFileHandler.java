package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * Used to provide a file-based log handler with daily log rotation.
 * Log files are created and rotated based on the current date.
 */
public class DailyFileHandler extends Handler {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Path logDir;
    private LocalDate currentDate;
    private FileHandler fileHandler;

    /**
     * Constructs a DailyFileHandler and initializes the log directory.
     *
     * @param logDir directory used to store log files
     * @throws IOException if the log directory or file cannot be created
     */
    public DailyFileHandler(String logDir) throws IOException {
        this.logDir = Paths.get(logDir);
        Files.createDirectories(this.logDir);
        rotateIfNeeded();
    }

    /**
     * Rotates the log file if the current date has changed.
     *
     * @throws IOException if the log file cannot be created or accessed
     */
    private void rotateIfNeeded() throws IOException {
        LocalDate today = LocalDate.now();
        if (!today.equals(currentDate)) {
            currentDate = today;

            if (fileHandler != null) {
                fileHandler.close();
            }

            String filename = DATE_FORMAT.format(today) + ".log";
            Path logFile = logDir.resolve(filename);

            fileHandler = new FileHandler(logFile.toString(), true);
            fileHandler.setFormatter(new SimpleFormatter());
        }
    }

    @Override
    public void publish(LogRecord record) {
        try {
            rotateIfNeeded();
            fileHandler.publish(record);
            fileHandler.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {
        if (fileHandler != null) {
            fileHandler.flush();
        }
    }

    @Override
    public void close() {
        if (fileHandler != null) {
            fileHandler.close();
        }
    }
}
