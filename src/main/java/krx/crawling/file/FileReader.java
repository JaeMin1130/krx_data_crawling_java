package krx.crawling.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FileReader {

    private FileReader(){}
    
    public static Properties read(final String path) {
        final Properties properties = new Properties();
        try (final FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Error reading configuration file: " + e.getMessage());
        }
        return properties;
    }
}
