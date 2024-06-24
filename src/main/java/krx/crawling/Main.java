package krx.crawling;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;
import java.util.TreeSet;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import krx.crawling.entity.Stock;
import krx.crawling.mapper.StockMapper;
import krx.crawling.repository.StockRepository;
import krx.crawling.service.StockDBService;
import krx.crawling.utils.FileWriterUtil;
import krx.crawling.utils.KrxCrawler;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        Set<Stock> stockSet = new TreeSet<>();
        stockSet = KrxCrawler.execute();

        // Use StockRepository to handle database operations
        StockRepository stockRepository = new StockDBService();
        stockRepository.insertCrawledStocks(stockSet);

        // Save the extracted text to a file
        String filePath = "./app/stocks.txt";

        FileWriterUtil.writeStocksToFile(stockSet, filePath);
    }
}
