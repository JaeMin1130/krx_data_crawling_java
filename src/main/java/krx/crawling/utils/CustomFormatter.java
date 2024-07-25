package krx.crawling.utils;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        // Customize the log message format
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(record.getLevel()).append("] ");
        sb.append(formatMessage(record)).append("\n");
        return sb.toString();
    }
}

