package krx.crawling.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import krx.crawling.stocks.dto.BaseStockDto;
import krx.crawling.stocks.dto.FinanceStockDto;
import krx.crawling.stocks.dto.StockDtoBuilder;
import krx.crawling.stocks.entity.Stock;

public final class KrxCrawler {
    private final WebDriver driver;

    public KrxCrawler(WebDriver driver){
        this.driver = driver;
    }

    public Set<Stock> execute(LocalDate date) throws InterruptedException {
        String strDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<BaseStockDto> baseDtoList = crawlBaseStock(strDate);
        System.out.println("--------------Finish base data crawling--------------");

        List<FinanceStockDto> financeDataList = crawlFinanceStock(strDate);
        System.out.println("--------------Finish finance data crawling--------------");

        System.out.println("base: " + baseDtoList.size());
        System.out.println("finance: " + financeDataList.size());

        Set<Stock> stockSet = new TreeSet<>();

        Iterator<FinanceStockDto> financeIter = financeDataList.iterator();
        FinanceStockDto financeDto = financeIter.next();
        boolean isEqual;
        for (BaseStockDto baseDto : baseDtoList) {
            isEqual = baseDto.getCompanyName().equals(financeDto.getCompanyName());

            Stock stock = Stock.builder()
                    .companyName(baseDto.getCompanyName())
                    .marketCategory(baseDto.getMarketCategory())
                    .sector(baseDto.getSector())
                    .close(baseDto.getClose())
                    .tradingVolume(baseDto.getTradingVolume())
                    .tradingValue(baseDto.getTradingValue())
                    .marketCap(baseDto.getMarketCap())
                    .eps(isEqual ? financeDto.getEps() : null)
                    .per(isEqual ? financeDto.getPer() : null)
                    .bps(isEqual ? financeDto.getBps() : null)
                    .pbr(isEqual ? financeDto.getPbr() : null)
                    .dps(isEqual ? financeDto.getDps() : null)
                    .dy(isEqual ? financeDto.getDy() : null)
                    .date(date)
                    .build();

            stockSet.add(stock);

            if (isEqual && financeIter.hasNext())
                financeDto = financeIter.next();
        }

        return stockSet;
    }

    private <T> List<T> crawlStocks(String date, String url, StockDtoBuilder<T> builder, int fieldCount)
            throws InterruptedException {

        if (!isValidDate(date)) throw new IllegalArgumentException("Invalid date format: " + date);

        List<T> result = new ArrayList<>();
        System.out.println("Open a window for crawling. url : " + url);
        driver.get(url);
        System.out.println(driver.getTitle());

        boolean isPossible = setDate(date);
        if (!isPossible) {
            throw new IllegalStateException("주말 또는 휴장일(" + date + ")");
        }
        System.out.println("Finish setting date. Selected date is " + date);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("Wait until contents are loaded.");
        wait.until(d -> driver.findElement(By.cssSelector(".tui-grid-cell-content")));

        System.out.println("Start to crawl contents.");
        WebElement scrollArea = driver.findElement(By.cssSelector(".tui-grid-body-area"));

        int rowKey = 0;
        boolean isScrollable = true;
        while (true) {
            isScrollable = scroll(rowKey, scrollArea);

            List<WebElement> stockElements = driver
                    .findElements(By.cssSelector(String.format("[data-row-key=\"%d\"]", rowKey)));
            while (stockElements.size() != 0) {
                System.out.println(rowKey + ": " + stockElements.get(0).getText());

                List<String> values = new ArrayList<>();
                for (int idx = 0; idx < fieldCount; idx++) {
                    values.add(stockElements.get(idx).getText());
                }

                result.add(builder.build(values));

                stockElements = driver.findElements(By.cssSelector(String.format("[data-row-key='%d']", ++rowKey)));
            }

            if (!isScrollable) break;
        }

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
                    firstKey > rowKey ? "arguments[0].scrollBy(0, -10);" : "arguments[0].scrollBy(0, 100);",
                    dataArea);

            System.out.println("scrolling... tryCount :" + ++tryCount);
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

        // 1. 년, 월 맞추기
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
        System.out.println("Finish setting year and month.");

        // 2. 선택 가능한 날인지 확인(주말, 휴장일)
        List<WebElement> possibleDayList = calendar.findElements(By.tagName("a"));
        for (WebElement dayElement : possibleDayList) {
            int possibleDay = Integer.parseInt(dayElement.getText());
            if (desiredDay != possibleDay)
                continue;

            dayElement.click();
            driver.findElement(By.className("CI-CAL-CONFIRM-BTN")).click();
            return true;
        }

        System.out.println("Inserted date is not available.");
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
            return false;
        }
    }
}