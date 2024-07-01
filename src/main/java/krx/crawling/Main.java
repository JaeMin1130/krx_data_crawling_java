package krx.crawling;

import static java.time.LocalDate.now;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TreeSet;

import krx.crawling.stocks.entity.Stock;
import krx.crawling.stocks.repository.StockRepository;
import krx.crawling.stocks.repository.StockRepositoryImpl;
import krx.crawling.utils.KrxCrawler;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        Set<Stock> stockSet = new TreeSet<>();
        StockRepository stockRepo = new StockRepositoryImpl();
        int maxCount = 5;
        // int maxCount = Integer.parseInt(args[0]);
        int count = 0;
        int idx = 0;
        while (count < maxCount) {
            try {
                LocalDate selectedDate = now().plusDays(idx--);
                stockSet = KrxCrawler.execute(selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } catch (IllegalArgumentException e) {
                System.out.println(e.toString());
                continue;
            }
            stockRepo.insertCrawledStocks(stockSet);
            count++;
        }

    }
}
