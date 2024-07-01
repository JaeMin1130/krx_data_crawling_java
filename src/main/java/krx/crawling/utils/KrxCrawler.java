package krx.crawling.utils;

import static java.time.LocalDateTime.now;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import krx.crawling.stocks.entity.Stock;

public final class KrxCrawler {
    private static WebDriver driver;
    private static JavascriptExecutor js;

    private KrxCrawler() {
        throw new AssertionError();
    }

    private static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static Set<Stock> execute(String date) throws InterruptedException {
        startChromeDriver();

        if (!isValidDate(date)) throw new IllegalArgumentException("Invalid date format: " + date);

        List<ArrayList<String>> baseDataList = crawlContents(
                "http://data.krx.co.kr/contents/MMC/ISIF/isif/MMCISIF001.cmd", date);
        System.out.println("--------------Finish base data crawling--------------");

        List<ArrayList<String>> financeDataList = crawlContents(
                "http://data.krx.co.kr/contents/MMC/ISIF/isif/MMCISIF002.cmd", date);
        System.out.println("--------------Finish finance data crawling--------------");

        Set<Stock> stockSet = new TreeSet<>();
        int financeIdx = 0;
        int unequalCount = 0;
        for (int i = 0; i < baseDataList.size(); i++) {
            Iterator<String> baseDataIter = baseDataList.get(i).iterator();
            Iterator<String> financeDataIter = financeDataList.get(financeIdx++).iterator();

            boolean isEqual = true;
            String companyNameBase = baseDataIter.next();
            String companyNameFinance = financeDataIter.next();
            if (companyNameFinance.contains("락"))
                continue;

            if (!companyNameFinance.equals(companyNameBase)) {
                isEqual = false;
                financeIdx--;
                unequalCount++;
            }

            financeDataIter.next(); // 중복되는 close 데이터 스킵

            Stock stock = Stock.builder()
                    // .id(i)
                    .company(companyNameBase)
                    .marketCategory(baseDataIter.next())
                    .sector(baseDataIter.next())
                    .close(baseDataIter.next())
                    .volume(baseDataIter.next())
                    .tradingValue(baseDataIter.next())
                    .marketCap(baseDataIter.next())
                    .eps(isEqual ? financeDataIter.next() : null)
                    .per(isEqual ? financeDataIter.next() : null)
                    .bps(isEqual ? financeDataIter.next() : null)
                    .pbr(isEqual ? financeDataIter.next() : null)
                    .dps(isEqual ? financeDataIter.next() : null)
                    .dy(isEqual ? financeDataIter.next() : null)
                    .date(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .build();

            stockSet.add(stock);
        }

        driver.quit();
        
        System.out.println("Total number of stocks unequal is " + unequalCount);
        return stockSet;
    }

    private static List<ArrayList<String>> crawlContents(String url, String date) throws InterruptedException {
        System.out.println("start data crawling. url : " + url);
        openBrowser(url);

        boolean isPossible = setDate(date);
        if (!isPossible) throw new IllegalArgumentException("주말 또는 휴장일(" + date + ")");
        System.out.println("Finish setting date. Selected date is " + date);
        
        List<WebElement> bodyArea = driver.findElements(By.cssSelector(".tui-grid-body-area"));
        WebElement nameArea = bodyArea.get(0);
        WebElement dataArea = bodyArea.get(1);

        List<WebElement> companyList;
        List<WebElement> dataElementList;

        int rowCount = 0;
        List<ArrayList<String>> result = new ArrayList<>();
        while (scroll(rowCount, dataArea)) {
            companyList = nameArea.findElements(By.cssSelector(".tui-grid-cell"));
            dataElementList = dataArea.findElements(By.cssSelector(".tui-grid-row-odd, .tui-grid-row-even"));

            for (int i = 0; i < dataElementList.size(); i++) {
                WebElement row = dataElementList.get(i);

                ArrayList<String> dataArr = new ArrayList<>();
                dataArr.add(companyList.get(i).getText());

                Iterator<WebElement> iter = row.findElements(By.cssSelector(".tui-grid-cell-content")).iterator();
                while (iter.hasNext()) {
                    dataArr.add(iter.next().getText());
                }
                result.add(dataArr);
            }
            if(result.size() > 10) break;
            rowCount += dataElementList.size();

            System.out.println("total number of rows : " + rowCount);
            System.out.printf("%.2f%% done\n", (double) rowCount / 2800 * 100);
        }

        return result;
    }

    private static void startChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run Chrome in headless mode(no UI)
        options.addArguments("--no-sandbox"); // Bypass OS security model

        driver = new ChromeDriver(options);
        js = (JavascriptExecutor) driver;

        System.out.println("Chrome driver is up and running.");
    }

    private static void openBrowser(String url) {
        // driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        driver.get(url);
        System.out.println(driver.getTitle());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        System.out.println("wait until contents are loaded.");
        wait.until(d -> driver.findElement(By.cssSelector(".tui-grid-cell-content")));
    }

    private static boolean scroll(int rowCount, WebElement dataArea) throws InterruptedException {
        int tryCount = 0;
        WebElement firstElement = driver.findElement(
                By.xpath("//*[@id='jsGrid']/div/div[1]/div[1]/div[2]/div/div[1]/table/tbody/tr[1]/td"));
        int firstKey = Integer.parseInt(firstElement.getAttribute("data-row-key"));

        while (firstKey != rowCount && tryCount < 20) {
            System.out.println("scrolling... tryCount :" + ++tryCount);
            js.executeScript(
                    firstKey > rowCount ? "arguments[0].scrollBy(0, -2);" : "arguments[0].scrollBy(0, 50);",
                    dataArea);

            Thread.sleep(20);

            firstElement = driver.findElement(
                    By.xpath("//*[@id='jsGrid']/div/div[1]/div[1]/div[2]/div/div[1]/table/tbody/tr[1]/td"));
            firstKey = Integer.parseInt(firstElement.getAttribute("data-row-key"));
        }

        return tryCount < 20 ? true : false;
    }

    private static boolean setDate(String date) {
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

    private static WebElement setYearMonth(WebElement calendar, int desiredVal, int selectedVal, String target) {
        while (desiredVal != selectedVal) {
            calendar.findElement(By.className(desiredVal > selectedVal ? "next" + target : "prev" + target)).click();
            calendar = driver.findElement(By.className("calendar"));
            selectedVal += desiredVal > selectedVal ? 1 : -1;
        }
        return calendar;
    }
}