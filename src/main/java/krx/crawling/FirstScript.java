package krx.crawling;

import java.io.IOException;
import java.util.Set;

public class FirstScript {

    public static void main(String[] args) throws InterruptedException, IOException {
        StockScraper scraper = new StockScraper();
        Set<Stock> stockSet = scraper.scrapeStocks();

        FileWriterUtil fileWriter = new FileWriterUtil();
        String filePath = "./app/stocks.txt";
        fileWriter.writeStocksToFile(stockSet, filePath);
    }
}
