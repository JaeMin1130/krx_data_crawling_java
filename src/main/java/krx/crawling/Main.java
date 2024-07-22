package krx.crawling;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import krx.crawling.stocks.entity.Stock;
import krx.crawling.stocks.repository.StockRepository;
import krx.crawling.stocks.repository.StockRepositoryImpl;
import krx.crawling.utils.KrxCrawler;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException, IOException {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                logger.info("Running a task...");
                saveData(args);
                logger.info("Finish the task.");

                logger.info(String.format("The next task will be executed %s at 16:00.", LocalDate.now().plusDays(1)));
            }
        };

        if (args.length == 0) {
            logger.info("Start to save initial datas");
            saveData(args);
            logger.info(String.format("Stock datas of past %s trading days were saved", args[3]));
        }

        long oneDay = 24 * 60 * 60 * 1000;
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime tomorrowAt16 = now.plusDays(0).withHour(16).withMinute(0).withSecond(0).withNano(0);
        Date startDate = Date.from(tomorrowAt16.toInstant());

        logger.info(String.format("A task will be executed at %s for the first time.", startDate));
        timer.schedule(task, startDate, oneDay);
    }

    private static void saveData(String[] args) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");

        try (ClosableWebDriver closableDriver = new ClosableWebDriver(new FirefoxDriver(options))) {
            WebDriver driver = closableDriver.getWebDriver();
            logger.info("FireFox driver is up and running.");

            int year = args.length == 0 ? LocalDate.now().getYear() : Integer.parseInt(args[0]);
            int month = args.length == 0 ? LocalDate.now().getMonthValue() : Integer.parseInt(args[1]);
            int day = args.length == 0 ? LocalDate.now().getDayOfMonth() : Integer.parseInt(args[2]);
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
                    // stockSet = krxCrawler.execute(selectedDate);
                    stockSet = krxCrawler.execute(LocalDate.of(2024, 7, 15));
                } catch (IllegalStateException e) {
                    logger.severe("IllegalStateException occurred: " + e.getMessage());
                    continue;
                } catch (IllegalArgumentException e) {
                    logger.severe("IllegalArgumentException occurred: " + e.getMessage());
                    break;
                } catch (Exception e) {
                    logger.severe("Exception occurred: " + e.getMessage());
                    break;
                }

                logger.info(String.format("Start to insert stock into DB. date: %s", selectedDate));
                stockRepo.insertCrawledStocks(stockSet);
                count++;
            }

            logger.info("All jobs are finished.");
        }

        logger.info("FireFox driver is closed.");
    }

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
