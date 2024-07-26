package krx.crawling;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
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

import krx.crawling.stocks.entity.Stock;
import krx.crawling.stocks.repository.StockRepository;
import krx.crawling.stocks.repository.StockRepositoryImpl;
import krx.crawling.utils.KrxCrawler;
import krx.crawling.utils.LoggerSetup;

public class Main {

    private static final Logger logger = LoggerSetup.getLogger();

    public static void main(String[] args) throws InterruptedException, IOException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable batchJob = () -> {
            logger.info("Running a batchJob...");
            logger.info("Current time is " + LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            
            saveData(new String[]{});
            
            logger.info("Finish the batchJob.");
            logger.info(String.format("The next batchJob will be executed %s at 16:00.", LocalDate.now().plusDays(1)));
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
                    
                    try{
                        logger.info("Running a liveJob...");
                        logger.info("Input value: " + Arrays.toString(input));
                        logger.info("Current time is " + LocalDateTime.now(ZoneId.of("Asia/Seoul")));

                        saveData(input);
                        logger.info("Finish the liveJob.");
                    }catch(NumberFormatException e){
                        logger.warning("Some inputs you entered are not a number!! Enter a input of a nuber format!!");
                        continue;
                    }catch(IllegalStateException e){
                        logger.warning(e.getMessage());
                        continue;
                    }catch(DateTimeException e){
                        logger.warning(e.getMessage());
                        continue;
                    }catch(Exception e){
                        logger.severe(e.getMessage());
                        continue;
                    }
                }
            }
        };
        
        // Schedule the batchJob to run at 16:00 every day
        long oneDay = 24 * 60 * 60 * 1000;
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime todayAt16 = now.plusDays(0).withHour(16).withMinute(0).withSecond(0).withNano(0);
        long initialDelay = Date.from(todayAt16.toInstant()).getTime() - System.currentTimeMillis();

        logger.info(String.format("A batchJob will be executed at %s for the first time.", todayAt16));
        scheduler.scheduleAtFixedRate(batchJob, initialDelay, oneDay, TimeUnit.MILLISECONDS);

        // Run the liveJob in the foreground
        Thread liveJobThread = new Thread(liveJob);
        liveJobThread.start();
    }

    private static void saveData(String[] args) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");

        try (ClosableWebDriver closableDriver = new ClosableWebDriver(new FirefoxDriver(options))) {
            WebDriver driver = closableDriver.getWebDriver();
            logger.info("Firefox driver is up and running.");

            int curYear = LocalDate.now().getYear();
            int year = args.length == 0 ? curYear : Integer.parseInt(args[0]);
            if(year < curYear - 4 || year > curYear + 4) throw new IllegalStateException("It is only possible to crawl within the current year ± 4.");

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

                logger.info(String.format("Start UPSERT, date: %s", selectedDate));
                int totalCount = stockRepo.upsertCrawledStocks(stockSet);
                logger.info(String.format("Finish UPSERT, totalCount: %s", totalCount));

                count++;
            }

            logger.info("All jobs are finished.");
        }

        logger.info("Firefox driver is closed.");
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
