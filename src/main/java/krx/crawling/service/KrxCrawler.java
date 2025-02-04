package krx.crawling.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import krx.crawling.model.dao.BaseStockDto;
import krx.crawling.model.dao.FinanceStockDto;
import krx.crawling.model.dao.StockDao;
import krx.crawling.model.dao.StockDaoBuilder;
import krx.crawling.model.entity.Stock;
import krx.crawling.model.entity.StockBuilderUtil;

public final class KrxCrawler {
    private static final Logger logger = Logger.getLogger(KrxCrawler.class.getName());
    private WebDriver driver;
    private WebDriverWait wait;

    public KrxCrawler(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public Set<Stock> execute(LocalDate date) throws InterruptedException {
        String strDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<BaseStockDto> baseDaoList = crawlBaseStock(strDate);
        logger.info("--------------Finish base data crawling--------------");

        List<FinanceStockDto> financeDaoList = crawlFinanceStock(strDate);
        logger.info("--------------Finish finance data crawling--------------");

        logger.info("base: " + baseDaoList.size());
        logger.info("finance: " + financeDaoList.size());

        Set<Stock> stockSet = new TreeSet<>();

        Iterator<FinanceStockDto> financeIter = financeDaoList.iterator();
        FinanceStockDto financeDao = financeIter.next();
        boolean isEqual;
        for (BaseStockDto baseDao : baseDaoList) {
            // isEqual = baseDao.getCompanyName().equals(financeDao.getCompanyName());
            isEqual = financeDao.getCompanyName().contains(baseDao.getCompanyName());

            StockDao stockDao = StockDao.builder()
                    .companyName(baseDao.getCompanyName())
                    .marketCategory(baseDao.getMarketCategory())
                    .sector(baseDao.getSector())
                    .close(baseDao.getClose())
                    .tradingVolume(baseDao.getTradingVolume())
                    .tradingValue(baseDao.getTradingValue())
                    .marketCap(baseDao.getMarketCap())
                    .eps(isEqual ? financeDao.getEps() : null)
                    .per(isEqual ? financeDao.getPer() : null)
                    .bps(isEqual ? financeDao.getBps() : null)
                    .pbr(isEqual ? financeDao.getPbr() : null)
                    .dps(isEqual ? financeDao.getDps() : null)
                    .dy(isEqual ? financeDao.getDy() : null)
                    .date(strDate)
                    .build();

            Stock stock = Stock.builder()
                    .companyName(stockDao.getCompanyName())
                    .marketCategory(stockDao.getMarketCategory())
                    .sector(stockDao.getSector())
                    .close(StockBuilderUtil.parseInteger(stockDao.getClose()))
                    .tradingVolume(StockBuilderUtil.parseLong(stockDao.getTradingVolume()))
                    .tradingValue(StockBuilderUtil.parseLong(stockDao.getTradingValue()))
                    .marketCap(StockBuilderUtil.parseLong(stockDao.getMarketCap()))
                    .eps(StockBuilderUtil.parseInteger(stockDao.getEps()))
                    .bps(StockBuilderUtil.parseInteger(stockDao.getBps()))
                    .dps(StockBuilderUtil.parseInteger(stockDao.getDps()))
                    // .per(StockBuilderUtil.parseDouble(stockDao.getPer()))
                    // .pbr(StockBuilderUtil.parseDouble(stockDao.getPbr()))
                    // .dy(StockBuilderUtil.parseDouble(stockDao.getDy()))
                    .date(LocalDate.parse(strDate))
                    .build();

            stockSet.add(stock);

            logger.info(stock.toString());

            if (isEqual && financeIter.hasNext())
                financeDao = financeIter.next();
        }

        return stockSet;
    }

    private <T> List<T> crawlStocks(String date, String url, StockDaoBuilder<T> builder, int fieldCount)
            throws InterruptedException {

        if (!isValidDate(date))
            throw new IllegalArgumentException("Invalid date format: " + date);

        List<T> result = new ArrayList<>();

        logger.info("Open a window for crawling. url: " + url);
        driver.get(url);
        logger.info(driver.getTitle());

        boolean isPossible = setDate(date);

        if (!isPossible)
            throw new IllegalStateException("Weekend or holiday (" + date + ")");

        logger.info("Finish setting date. Selected date is " + date);
        wait.until(d -> driver.findElement(By.cssSelector(".tui-grid-cell-content")));
        Thread.sleep(100);

        logger.info("Click a submit button.");
        wait.until(ExpectedConditions.elementToBeClickable(By.className("btnSubmit"))).click();
        Thread.sleep(100);

        logger.info("Wait until contents are loaded.");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loading-bar-wrap.small")));
        logger.info("Contents are loaded.");
        Thread.sleep(1000);

        logger.info("Start crawling contents.");
        WebElement scrollArea = driver.findElement(By.cssSelector(".tui-grid-body-area"));

        int rowKey = 0;
        boolean isScrollable = true;
        while (isScrollable) {
            isScrollable = scroll(rowKey, scrollArea);
            List<WebElement> stockElements = driver
                    .findElements(By.cssSelector(String.format("[data-row-key=\"%d\"]", rowKey)));

            while (stockElements.size() != 0) {
                // logger.info(rowKey + ": " + stockElements.get(0).getText());

                List<String> values = new ArrayList<>();
                for (int idx = 0; idx < fieldCount; idx++) {
                    values.add(stockElements.get(idx).getText());
                }
                T stock = builder.build(values);
                result.add(stock);
                logger.info(stock.toString());

                stockElements = driver.findElements(By.cssSelector(String.format("[data-row-key='%d']", ++rowKey)));
            }

        }

        logger.info("Finish crawling contents.");

        return result;
    }

    private List<BaseStockDto> crawlBaseStock(String date) throws InterruptedException {
        return crawlStocks(date,
                "http://data.krx.co.kr/contents/MMC/ISIF/isif/MMCISIF001.cmd",
                values -> BaseStockDto.builder()
                        .companyName(values.get(0))
                        .marketCategory(values.get(1))
                        .sector(values.get(2))
                        .close(values.get(3))
                        .change(values.get(4))
                        .fluctuationRate(values.get(5))
                        .tradingVolume(values.get(6))
                        .tradingValue(values.get(7))
                        .marketCap(values.get(8))
                        .build(),
                9);
    }

    private List<FinanceStockDto> crawlFinanceStock(String date) throws InterruptedException {
        return crawlStocks(date,
                "http://data.krx.co.kr/contents/MMC/ISIF/isif/MMCISIF002.cmd",
                values -> FinanceStockDto.builder()
                        .companyName(values.get(0))
                        .close(values.get(1))
                        .change(values.get(2))
                        .fluctuationRate(values.get(3))
                        .eps(values.get(4))
                        .per(values.get(5))
                        .bps(values.get(6))
                        .pbr(values.get(7))
                        .dps(values.get(8))
                        .dy(values.get(9))
                        .build(),
                10);
    }

    private boolean scroll(int rowKey, WebElement dataArea) throws InterruptedException {
        int tryCount = 0;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement firstElement = driver.findElement(
                By.xpath("//*[@id='jsGrid']/div/div[1]/div[1]/div[2]/div/div[1]/table/tbody/tr[1]/td"));
        int firstKey = Integer.parseInt(firstElement.getAttribute("data-row-key"));

        while (firstKey != rowKey && tryCount < 20) {
            js.executeScript(
                    firstKey > rowKey ? "arguments[0].scrollBy(0, -20);" : "arguments[0].scrollBy(0, 260);",
                    dataArea);

            tryCount++;
            // logger.info("scrolling... tryCount: " + tryCount);
            Thread.sleep(20);
            firstElement = driver.findElement(
                    By.xpath("//*[@id='jsGrid']/div/div[1]/div[1]/div[2]/div/div[1]/table/tbody/tr[1]/td"));
            firstKey = Integer.parseInt(firstElement.getAttribute("data-row-key"));
        }

        return tryCount < 20;
    }

    private boolean setDate(String date) {
        driver.findElement(By.className("CI-CAL-OPEN-BTN")).click();
        WebElement calendar = driver.findElement(By.className("calendar"));

        // 1. Year and Month
        int[] desiredDate = Stream.of(date.split("-")).mapToInt(Integer::parseInt).toArray();
        int desiredYear = desiredDate[0];
        int desiredMonth = desiredDate[1];
        int desiredDay = desiredDate[2];

        WebElement calTitle = calendar.findElement(By.className("calTit"));
        int[] yearMonth = Stream.of(calTitle.getText().split("\\.")).mapToInt(Integer::parseInt).toArray();
        int selectedYear = yearMonth[0];
        int selectedMonth = yearMonth[1];

        calendar = setYearMonth(calendar, desiredYear, selectedYear, "Year");
        calendar = setYearMonth(calendar, desiredMonth, selectedMonth, "Month");
        logger.info("Finish setting year and month.");

        // 2. Check if day is selectable
        List<WebElement> possibleDayList = calendar.findElements(By.tagName("a"));
        for (WebElement dayElement : possibleDayList) {
            int possibleDay = Integer.parseInt(dayElement.getText());

            if (desiredDay != possibleDay)
                continue;

            dayElement.click();
            driver.findElement(By.className("CI-CAL-CONFIRM-BTN")).click();
            return true;
        }

        logger.warning("Inserted date is not available.");
        return false;
    }

    private WebElement setYearMonth(WebElement calendar, int desiredVal, int selectedVal, String target) {
        while (desiredVal != selectedVal) {
            calendar.findElement(By.className(desiredVal > selectedVal ? "next" + target : "prev" + target)).click();
            calendar = driver.findElement(By.className("calendar"));
            selectedVal += desiredVal > selectedVal ? 1 : -1;
        }

        return calendar;
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            logger.log(Level.SEVERE, "Date parsing failed for: " + date, e);
            return false;
        }
    }
}