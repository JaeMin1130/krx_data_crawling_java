package krx.crawling.log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerSetup {
    private static Logger LOGGER = Logger.getLogger(LoggerSetup.class.getName());
    private static boolean isInitialized = false;

    public static Logger getLogger() {
        if (!isInitialized) {
            setLogger();
            isInitialized = true;
        }
        return LOGGER;
    }
    public static Logger setLogger() {
        try {
            String logDir = "logs";
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String logFileName = logDir + "/krx_" + date + ".log";

            // Create the logs directory if it doesn't exist
            Path logPath = Paths.get(logDir);
            if (Files.notExists(logPath)) {
                Files.createDirectories(logPath);
            }

            // Set up the FileHandler
            FileHandler fileHandler = new FileHandler(logFileName, true);
            fileHandler.setFormatter(new CustomLogFormatter());
            fileHandler.setEncoding("UTF-8");

            // Configure logger
            LOGGER.setLevel(Level.INFO);
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(false); // Disable console logging

        } catch (IOException e) {
            e.printStackTrace();
        }

        return LOGGER;
    }
}
