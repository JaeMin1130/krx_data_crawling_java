package krx.crawling.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import krx.crawling.domain.stocks.entity.Stock;

public class FileWriterUtil {

    public static void writeStocksToFile(Set<Stock> stockSet, String filePath) {
        // Ensure the directory exists before writing the file
        File file = new File(filePath);
        file.getParentFile().mkdirs(); // Create directories if they don't exist

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (Stock stock : stockSet) {
                out.println(stock.toString());
            }
            System.out.println("Stocks written to file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
