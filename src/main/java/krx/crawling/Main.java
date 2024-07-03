package krx.crawling;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import krx.crawling.stocks.entity.Stock;
import krx.crawling.stocks.repository.StockRepository;
import krx.crawling.stocks.repository.StockRepositoryImpl;
import krx.crawling.utils.KrxCrawler;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run Chrome in headless mode(no UI)
        options.addArguments("--no-sandbox"); // Bypass OS security model
        
        try (ClosableWebDriver closableDriver = new ClosableWebDriver(new ChromeDriver(options))) {
            WebDriver driver = closableDriver.getWebDriver();
            System.out.println("Chrome driver is up and running.");

            // int year = Integer.parseInt(args[0]);
            // int month = Integer.parseInt(args[1]);
            // int day = Integer.parseInt(args[2]);
            // LocalDate insertedDate = LocalDate.of(year, month, day);
            // int maxCount = Integer.parseInt(args[3]);
            
            LocalDate insertedDate = LocalDate.of(2024, 7, 3);
            int maxCount = 2;

            int count = 0;
            int idx = 0;
            
            KrxCrawler krxCrawler = new KrxCrawler(driver);
            StockRepository stockRepo = new StockRepositoryImpl();
            Set<Stock> stockSet = new TreeSet<>();
            
            while (count < maxCount) {
                LocalDate selectedDate = insertedDate.plusDays(idx--);
                try {
                    stockSet = krxCrawler.execute(selectedDate);
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
        
        System.out.println("Chrome driver is closed.");
    }
    
    // Wrapper class for WebDriver to implement AutoCloseable
    static class ClosableWebDriver implements AutoCloseable {
        private final WebDriver webDriver;

        public ClosableWebDriver(WebDriver webDriver) {
            this.webDriver = webDriver;
        }

        public WebDriver getWebDriver() {
            return webDriver;
        }

        @Override
        public void close() {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
    }
}
