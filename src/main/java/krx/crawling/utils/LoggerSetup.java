package krx.crawling.utils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggerSetup {
    private static final Logger logger = Logger.getLogger(LoggerSetup.class.getName());

    public static Logger getLogger() {
        setupLogger();
        return logger;
    }

    private static void setupLogger() {
        try {
            // Remove default handlers
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }

            // Create log directory if it doesn't exist
            String logDir = "./volume/logs/";
            File directory = new File(logDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Add custom FileHandler with date-based filename
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            FileHandler fileHandler = new FileHandler(logDir + "krx_" + date + ".log", true);
            fileHandler.setFormatter(new CustomFormatter());
            rootLogger.addHandler(fileHandler);

            // Add ConsoleHandler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new CustomFormatter());
            rootLogger.addHandler(consoleHandler);
            
        } catch (IOException e) {
            System.err.println("Could not configure logging.");
            e.printStackTrace();
        }
    }
}
