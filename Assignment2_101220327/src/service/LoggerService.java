package service;

import common.ConfigLoader;
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

    private static final boolean ENABLE_FILE = ConfigLoader.getInstance().ifLogOutput();
    private static final Level CONSOLE_LEVEL = ConfigLoader.getInstance().getConsoleLevel();
    private static final Level FILE_LEVEL = ConfigLoader.getInstance().getFileLevel();

    private static Logger logger;

    /**
     * Returns a shared logger instance.
     * The logger is initialized on first access and reused thereafter.
     *
     * @param enableFile whether file logging should be enabled
     * @return shared logger instance
     */
    public static Logger getLogger(boolean enableFile, Level consoleLogLevel, Level fileLogLevel) {
        if (logger != null) {
            return logger;
        }

        logger = Logger.getLogger("GlobalLogger");
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);

        //Console Handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(consoleLogLevel);
        consoleHandler.setFormatter(new ColorConsoleFormatter());
        logger.addHandler(consoleHandler);

        //File Handler
        if (enableFile) {
            try {
                Handler fileHandler = new DailyFileHandler("logs");
                fileHandler.setLevel(fileLogLevel);
                logger.addHandler(fileHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return logger;
    }

    public static Logger getLogger() {
        return getLogger(ENABLE_FILE, CONSOLE_LEVEL, FILE_LEVEL);
    }
}
