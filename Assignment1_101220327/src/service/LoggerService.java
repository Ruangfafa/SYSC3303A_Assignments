package service;

import util.ColorConsoleFormatter;
import util.DailyFileHandler;

import java.io.IOException;
import java.util.logging.*;

public class LoggerService {

    private static Logger logger;

    public static Logger getLogger(boolean enableFile) {
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
