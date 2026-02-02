package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Used to format log messages for console output with color coding.
 */
public class ColorConsoleFormatter extends Formatter {

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Formats a log record into a colored string for console output.
     *
     * @param record log record to format
     * @return formatted log message
     */
    @Override
    public String format(LogRecord record) {
        String color = switch (record.getLevel().getName()) {
            case "INFO" -> GREEN;
            case "WARNING" -> YELLOW;
            case "SEVERE" -> RED;
            case "FINE", "FINER", "FINEST" -> CYAN;
            default -> RESET;
        };

        return String.format(
                "%s%s [%s][%s]%s%s%n",
                color,
                TIME_FORMAT.format(LocalDateTime.now()),
                record.getLevel().getName(),
                Thread.currentThread().getName(),
                record.getMessage(),
                RESET
        );
    }
}
