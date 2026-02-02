package service;

import util.ColorConsoleFormatter;
import util.DailyFileHandler;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to provide a shared logger instance for the application.
 * The logger supports console output and optional file-based logging.
 */
public class LoggerService {

    private static Logger logger;

    /**
     * Returns a shared logger instance.
     * The logger is initialized on first access and reused thereafter.
     *
     * @param enableFile whether file logging should be enabled
     * @return shared logger instance
     */
    public static synchronized Logger getLogger(boolean enableFile) {
        if (logger != null) {
            return logger;
        }

        logger = Logger.getLogger("GlobalLogger");
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);

        //Console Handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new ColorConsoleFormatter());
        logger.addHandler(consoleHandler);

        //File Handler
        if (enableFile) {
            try {
                Handler fileHandler = new DailyFileHandler("logs");
                fileHandler.setLevel(Level.ALL);
                logger.addHandler(fileHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return logger;
    }
}
