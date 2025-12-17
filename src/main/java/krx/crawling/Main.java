package krx.crawling;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;
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
        saveData(new String[] {});
    }

    private static void saveData(String[] args) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");

        try (ClosableWebDriver closableDriver = new ClosableWebDriver(new FirefoxDriver(options))) {

            WebDriver driver = closableDriver.getWebDriver();
            KrxCrawler krxCrawler = new KrxCrawler(driver);
            Set<Stock> stockSet = new TreeSet<>();
            LocalDate today = LocalDate.now();
            logger.info("Firefox driver is up and running.");

            try {
                stockSet = krxCrawler.execute(today);
            } catch (IllegalStateException e) {
                logger.severe("IllegalStateException occurred: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                logger.severe("IllegalArgumentException occurred: " + e.getMessage());
            } catch (Exception e) {
                logger.severe("Exception occurred: " + e.getMessage());
            }

            logger.info(String.format("[%s]", LocalDateTime.now())
                    + String.format("Start UPSERT stocks, date: %s", today));
            DBService.upsertCrawledStocks(stockSet);
            logger.info(String.format("[%s]", LocalDateTime.now()) + "Finish UPSERT stocks");

            logger.info(String.format("[%s]", LocalDateTime.now())
                    + String.format("Start UPSERT indicators, date: %s", today));
            DBService.upsertIndicator();
            logger.info(String.format("[%s]", LocalDateTime.now()) + "Finish UPSERT indicators");

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
}
