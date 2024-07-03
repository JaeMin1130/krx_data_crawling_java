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
        int maxCount = 1;
        // int maxCount = Integer.parseInt(args[0]);
        int count = 0;
        int idx = 0;
        while (count < maxCount) {
            LocalDate selectedDate = now().plusDays(idx--);
            try {
                stockSet = KrxCrawler.execute(selectedDate);
            } catch (IllegalStateException e) {
                System.out.println(e.toString());
                continue;
            } catch (IllegalArgumentException e) {
                System.out.println(e.toString());
                break;
            }
            System.out.println("Start to insert stock into DB. date: " + selectedDate);
            stockRepo.insertCrawledStocks(stockSet);
            count++;
        }

        System.out.println("All jobs are finished.");

    }
}
