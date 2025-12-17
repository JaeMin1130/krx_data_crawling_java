package krx.crawling.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomLogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        // Customize the log message format
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(record.getLevel()).append("] ");
        sb.append("[").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("] ");
        sb.append(formatMessage(record)).append("\n");
        return sb.toString();
    }
}