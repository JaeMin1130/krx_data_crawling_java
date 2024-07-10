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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import krx.crawling.stocks.entity.Stock;
import krx.crawling.stocks.repository.StockRepository;
import krx.crawling.stocks.repository.StockRepositoryImpl;
import krx.crawling.utils.KrxCrawler;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Running a task...");
                saveData(args);
                System.out.println("Finish the task.");
                System.out.println("The next task will be executed tomorrow at 4:00 PM.");
            }
        };

        if (args.length != 0) {
            System.out.println("Start to save initial datas");
            saveData(args);
            System.out.println("Stock datas of past 20 trading days were saved");
        }

        long oneDay = 24 * 60 * 60 * 1000;
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime tomorrowAt16 = now.plusDays(0).withHour(16).withMinute(30).withSecond(0).withNano(0);
        Date startDate = Date.from(tomorrowAt16.toInstant());

        System.out.println("A task will be executed at " + startDate + " for the first time.");
        timer.schedule(task, startDate, oneDay);
    }

    private static void saveData(String[] args) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless"); // Run FireFox in headless mode(no UI)
        options.addArguments("--no-sandbox"); // Bypass OS security model

        try (ClosableWebDriver closableDriver = new ClosableWebDriver(new FirefoxDriver(options))) {
            WebDriver driver = closableDriver.getWebDriver();
            System.out.println("FireFox driver is up and running.");

            // default 값 설정: 오늘 날짜, 하루치
            int year = args.length == 0 ? now().getYear() : Integer.parseInt(args[0]);
            int month = args.length == 0 ? now().getMonthValue() : Integer.parseInt(args[1]);
            int day = args.length == 0 ? now().getDayOfMonth() : Integer.parseInt(args[2]);
            int numOfDays = args.length == 0 ? 1 : Integer.parseInt(args[3]);

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
                    continue;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

                System.out.println("Start to insert stock into DB. date: " + selectedDate);
                stockRepo.insertCrawledStocks(stockSet);
                count++;
            }

            System.out.println("All jobs are finished.");
        }

        System.out.println("FireFox driver is closed.");
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
