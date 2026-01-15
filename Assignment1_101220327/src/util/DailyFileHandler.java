package util;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class DailyFileHandler extends Handler {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Path logDir;
    private LocalDate currentDate;
    private FileHandler fileHandler;

    public DailyFileHandler(String logDir) throws IOException {
        this.logDir = Paths.get(logDir);
        Files.createDirectories(this.logDir);
        rotateIfNeeded();
    }

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
