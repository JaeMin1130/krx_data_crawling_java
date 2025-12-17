package krx.crawling;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import krx.crawling.jdbc.DBService;
import krx.crawling.log.LoggerSetup;
import krx.crawling.model.entity.Stock;
import krx.crawling.service.KrxCrawler;

public class Main {

    private static final Logger logger = LoggerSetup.getLogger();

    public static void main(String[] args) throws InterruptedException, IOException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable batchJob = () -> {
            var startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            logger.info("Running a batchJob...");
            logger.info("Current time is " + startTime);

            saveData(new String[] {});

            logger.info("Finish the batchJob.");
            logger.info(String.format("The next batchJob will be executed tomorrow."));
        };

        Runnable liveJob = () -> {
            try (Scanner sc = new Scanner(System.in)) {
                while (true) {
                    String[] input = new String[4];

                    System.out.println("Enter a year");
                    input[0] = sc.nextLine().trim();
                    System.out.println("Enter a month");
                    input[1] = sc.nextLine().trim();
                    System.out.println("Enter a day");
                    input[2] = sc.nextLine().trim();
                    System.out.println("Enter a number of days to crawl");
                    input[3] = sc.nextLine().trim();

                    try {
                        logger.info("Running a liveJob...");
                        logger.info("Input value: " + Arrays.toString(input));
                        logger.info("Current time is " + LocalDateTime.now(ZoneId.of("Asia/Seoul")));

                        saveData(input);
                        logger.info("Finish the liveJob.");
                    } catch (NumberFormatException e) {
                        logger.severe("Some inputs you entered are not a number!! Enter them as a number format!!");
                        continue;
                    } catch (IllegalStateException e) {
                        logger.severe(e.getMessage());
                        continue;
                    } catch (DateTimeException e) {
                        logger.severe(e.getMessage());
                        continue;
                    } catch (Exception e) {
                        logger.severe(e.getMessage());
                        continue;
                    }
                }
            }
        };

        // 배치: open(09:30), close(16:00)
        long oneDay = 24 * 60 * 60 * 1000;
        logger.info(String.format("A batchJob will be executed at 09:30 after market opened."));
        long openDelay = calculateInitialDelay(9, 30);
        scheduler.scheduleAtFixedRate(batchJob, openDelay, oneDay, TimeUnit.SECONDS);

        logger.info(String.format("A batchJob will be executed at 16:00 after market closed."));
        long closeDelay = calculateInitialDelay(16, 0);
        scheduler.scheduleAtFixedRate(batchJob, closeDelay, oneDay, TimeUnit.SECONDS);

        // 배치: 로그 작업
        long logDelay = calculateInitialDelay(0, 0);
        scheduler.scheduleAtFixedRate(LoggerSetup::setLogger, logDelay, oneDay, TimeUnit.SECONDS);
        // 실시간 작업
        Thread liveJobThread = new Thread(liveJob);
        liveJobThread.start();
    }

    private static void saveData(String[] args) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");

        try (ClosableWebDriver closableDriver = new ClosableWebDriver(new FirefoxDriver(options))) {
            int curYear = LocalDate.now().getYear();
            int year = args.length == 0 ? curYear : Integer.parseInt(args[0]);
            if (year < curYear - 4 || year > curYear + 4)
                throw new IllegalStateException("It is only possible to crawl within the current year ± 4.");

            int month = args.length == 0 ? LocalDate.now().getMonthValue() : Integer.parseInt(args[1]);
            int day = args.length == 0 ? LocalDate.now().getDayOfMonth() : Integer.parseInt(args[2]);
            int numOfDays = args.length == 0 ? 1 : Integer.parseInt(args[3]);

            int count = 0;
            int idx = 0;
            LocalDate insertedDate = LocalDate.of(year, month, day);

            WebDriver driver = closableDriver.getWebDriver();
            logger.info("Firefox driver is up and running.");

            KrxCrawler krxCrawler = new KrxCrawler(driver);
            Set<Stock> stockSet = new TreeSet<>();

            while (count < numOfDays) {
                LocalDate selectedDate = insertedDate.plusDays(idx--);

                try {
                    stockSet = krxCrawler.execute(selectedDate);
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

                logger.info(String.format("[%s]", LocalDateTime.now())
                        + String.format("Start UPSERT stocks, date: %s", selectedDate));
                DBService.upsertCrawledStocks(stockSet);
                logger.info(String.format("[%s]", LocalDateTime.now()) + "Finish UPSERT stocks");

                logger.info(String.format("[%s]", LocalDateTime.now())
                        + String.format("Start UPSERT indicators, date: %s", selectedDate));
                DBService.upsertIndicator();
                logger.info(String.format("[%s]", LocalDateTime.now()) + "Finish UPSERT indicators");

                count++;
            }

            logger.info("All jobs are finished.");
        }

        logger.info("Firefox driver is closed.");
    }

    private static class ClosableWebDriver implements AutoCloseable {
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

    // 지정 시간과 현재 시간의 차이(초 단위)
    private static long calculateInitialDelay(int targetHour, int targetMinute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(targetHour).withMinute(targetMinute).withSecond(0).withNano(0);

        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        ZonedDateTime nowZoned = ZonedDateTime.of(now, ZoneId.systemDefault());
        ZonedDateTime nextRunZoned = ZonedDateTime.of(nextRun, ZoneId.systemDefault());
        return TimeUnit.MILLISECONDS
                .toSeconds(nextRunZoned.toInstant().toEpochMilli() - nowZoned.toInstant().toEpochMilli());
    }
}
