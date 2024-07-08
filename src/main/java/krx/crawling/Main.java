package krx.crawling;

import static java.time.LocalDate.now;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
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
        Timer timer = new Timer();

        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                saveData(args, 20);
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                saveData(args, args.length == 3 ? 1 : Integer.parseInt(args[3]));
            }
        };

        System.out.println("Task1 is executing.");
        task1.run();
        System.out.println("Task1 is finished.");

        long oneDay = 24 * 60 * 60 * 1000;
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime tomorrowAt17 = now.plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        Date startDate = Date.from(tomorrowAt17.toInstant());

        System.out.println("Task2 will be executed at " + startDate + " for the first time.");
        timer.schedule(task2, startDate, oneDay);
    }

    private static void saveData(String[] args, int numOfDays) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run Chrome in headless mode(no UI)
        options.addArguments("--no-sandbox"); // Bypass OS security model

        try (ClosableWebDriver closableDriver = new ClosableWebDriver(new ChromeDriver(options))) {
            WebDriver driver = closableDriver.getWebDriver();
            System.out.println("Chrome driver is up and running.");

            // default 값 설정: 오늘 날짜, 하루치
            int year = args.length == 0 ? now().getYear() : Integer.parseInt(args[0]);
            int month = args.length == 0 ? now().getMonthValue() : Integer.parseInt(args[1]);
            int day = args.length == 0 ? now().getDayOfMonth() : Integer.parseInt(args[2]);

            int count = 0;
            int idx = 0;
            LocalDate insertedDate = LocalDate.of(year, month, day);

            KrxCrawler krxCrawler = new KrxCrawler(driver);
            StockRepository stockRepo = new StockRepositoryImpl();
            Set<Stock> stockSet = new TreeSet<>();

            while (count < numOfDays) {
                LocalDate selectedDate = insertedDate.plusDays(idx--);
                try {
                    stockSet = krxCrawler.execute(selectedDate);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    ;
                    continue;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    ;
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
