package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class ColorConsoleFormatter extends Formatter {

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                "%s%s [%s] %s%s%n",
                color,
                TIME_FORMAT.format(LocalDateTime.now()),
                record.getLevel().getName(),
                record.getMessage(),
                RESET
        );
    }
}
