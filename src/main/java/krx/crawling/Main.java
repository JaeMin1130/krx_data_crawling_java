package krx.crawling;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        Set<Stock> stockSet = new TreeSet<>();

        stockSet = KrxCrawler.execute();
        // stockSet = Crawler.crawlContents("http://data.krx.co.kr/contents/MMC/ISIF/isif/MMCISIF002.cmd");

        // Save the extracted text to a file
        String filePath = "./app/stocks.txt";

        FileWriterUtil.writeStocksToFile(stockSet, filePath);
    }
}
