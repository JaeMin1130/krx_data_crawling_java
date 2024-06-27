package krx.crawling.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import static java.time.LocalDateTime.now;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import krx.crawling.domain.stocks.entity.Stock;

public final class KrxCrawler {
    private static WebDriver driver;
    private static JavascriptExecutor js;

    private KrxCrawler() {
        throw new AssertionError();
    }

    public static Set<Stock> execute() throws InterruptedException {
        startChromeDriver();

        List<ArrayList<String>> baseDataList = crawlContents(
                "http://data.krx.co.kr/contents/MMC/ISIF/isif/MMCISIF001.cmd");
        System.out.println("--------------Finish base data crawling--------------");

        List<ArrayList<String>> financeDataList = crawlContents(
                "http://data.krx.co.kr/contents/MMC/ISIF/isif/MMCISIF002.cmd");
        System.out.println("--------------Finish finance data crawling--------------");

        Set<Stock> stockSet = new TreeSet<>();
        int financeIdx = 0;
        for (int i = 0; i < baseDataList.size(); i++) {
            Iterator<String> baseDataIter = baseDataList.get(i).iterator();
            Iterator<String> financeDataIter = financeDataList.get(financeIdx++).iterator();

            boolean isEqual = true;
            String companyName = baseDataIter.next();
            if (!financeDataIter.next().equals(companyName)) {
                isEqual = false;
                financeIdx--;
            }

            financeDataIter.next(); // 중복되는 close 데이터 스킵

            Stock stock = Stock.builder()
                    // .id(i)
                    .company(companyName)
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
                    .date(now())
                    .build();

            stockSet.add(stock);
        }
        return stockSet;
    }

    private static void startChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run Chrome in headless mode(no UI)
        options.addArguments("--no-sandbox"); // Bypass OS security model

        driver = new ChromeDriver(options);
        js = (JavascriptExecutor) driver;

        System.out.println("Chrome driver is up and running.");
    }

    private static List<ArrayList<String>> crawlContents(String url) throws InterruptedException {
        System.out.println("start data crawling. url : " + url);
        openBrowser(url);

        List<WebElement> bodyArea = driver.findElements(By.cssSelector(".tui-grid-body-area"));
        WebElement nameArea = bodyArea.get(0);
        WebElement dataArea = bodyArea.get(1);

        List<WebElement> companyList;
        List<WebElement> dataElementList;

        int rowCount = 0;
        List<ArrayList<String>> result = new ArrayList<>();
        while (scroll(rowCount, dataArea, 30)) {
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

            rowCount += dataElementList.size();

            System.out.println("total number of rows : " + rowCount);
            System.out.printf("%.2f%% done\n", (double) rowCount / 2800 * 100);
        }

        return result;
    }

    private static void openBrowser(String url) {
        // driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        driver.get(url);
        System.out.println(driver.getTitle());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        System.out.println("wait until contents are loaded.");
        wait.until(d -> driver.findElement(By.cssSelector(".tui-grid-cell-content")));
    }

    private static boolean scroll(int rowCount, WebElement dataArea, int maxTryCount) throws InterruptedException {
        int tryCount = 0;
        WebElement firstElement = driver.findElement(
                By.xpath("//*[@id='jsGrid']/div/div[1]/div[1]/div[2]/div/div[1]/table/tbody/tr[1]/td"));
        int firstKey = Integer.parseInt(firstElement.getAttribute("data-row-key"));

        while (firstKey != rowCount && tryCount < maxTryCount) {
            System.out.println("scrolling... tryCount :" + ++tryCount);
            js.executeScript(
                    firstKey > rowCount ? "arguments[0].scrollBy(0, -2);" : "arguments[0].scrollBy(0, 50);",
                    dataArea);

            Thread.sleep(20);

            firstElement = driver.findElement(
                    By.xpath("//*[@id='jsGrid']/div/div[1]/div[1]/div[2]/div/div[1]/table/tbody/tr[1]/td"));
            firstKey = Integer.parseInt(firstElement.getAttribute("data-row-key"));
        }

        return tryCount < maxTryCount ? true : false;
    }

}
