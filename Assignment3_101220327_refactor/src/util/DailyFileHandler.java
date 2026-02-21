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

public class DailyFileHandler extends Handler {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Path logDir;
    private LocalDate currentDate;
    private FileHandler fileHandler;

    public DailyFileHandler(String logDir) throws IOException {
        this.logDir = Paths.get(logDir);
        Files.createDirectories(this.logDir);
        this.rotateIfNeeded();
    }

    private void rotateIfNeeded() throws IOException {
        LocalDate today = LocalDate.now();
        if (!today.equals(this.currentDate)) {
            this.currentDate = today;
            if (this.fileHandler != null) {
                this.fileHandler.close();
            }

            String filename = DATE_FORMAT.format(today) + ".log";
            Path logFile = this.logDir.resolve(filename);
            this.fileHandler = new FileHandler(logFile.toString(), true);
            this.fileHandler.setFormatter(new SimpleFormatter());
        }

    }

    public void publish(LogRecord record) {
        try {
            this.rotateIfNeeded();
            this.fileHandler.publish(record);
            this.fileHandler.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void flush() {
        if (this.fileHandler != null) {
            this.fileHandler.flush();
        }

    }

    public void close() {
        if (this.fileHandler != null) {
            this.fileHandler.close();
        }

    }
}