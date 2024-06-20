package krx.crawling;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class Crawler {
    // private final Crawler crawler = new Crawler();
    private static WebDriver driver;
    private static JavascriptExecutor js;

    private Crawler() { throw new AssertionError();} 

    private static void startChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run Chrome in headless mode(no UI)
        options.addArguments("--no-sandbox"); // Bypass OS security model
        
        driver = new ChromeDriver(options);
        js = (JavascriptExecutor) driver;
    }

    static Set<Stock> crawlContents(String url) throws InterruptedException {

        startChromeDriver();
        
        openBrowser(url);

        List<WebElement> bodyArea = driver.findElements(By.cssSelector(".tui-grid-body-area"));
        WebElement nameArea = bodyArea.get(0);
        WebElement scrollArea = bodyArea.get(1);

        List<WebElement> companyList = nameArea.findElements(By.cssSelector(".tui-grid-cell"));
        List<WebElement> infoList = scrollArea
                .findElements(By.cssSelector(".tui-grid-row-odd, .tui-grid-row-even"));
        Set<Stock> stockSet = new TreeSet<>();

        int tryCount = 0;
        while (tryCount < 1) {

            tryCount = scroll(companyList, stockSet, scrollArea);

            companyList = nameArea.findElements(By.cssSelector(".tui-grid-cell"));
            infoList = scrollArea.findElements(By.cssSelector(".tui-grid-row-odd, .tui-grid-row-even"));

            for (int j = 0; j < companyList.size(); j++) {
                List<WebElement> detailInfoList = infoList.get(j)
                        .findElements(By.cssSelector(".tui-grid-cell-content"));

                List<String> stockDataList = new ArrayList<>();
                for (int k = 0; k < detailInfoList.size(); k++) {
                    stockDataList.add(detailInfoList.get(k).getText());
                }

                int idx = 0;
                String name = companyList.get(j).getText();
                int key = Integer.parseInt(companyList.get(j).getAttribute("data-row-key"));
                Stock stock = Stock.builder()
                        .id(key)
                        .company(name)
                        .marketCategory(stockDataList.get(idx++))
                        .sector(stockDataList.get(idx++))
                        .close(stockDataList.get(idx++))
                        .volume(stockDataList.get(idx++))
                        .tradingValue(stockDataList.get(idx++))
                        .marketCap(stockDataList.get(idx++))
                        .build();

                stockSet.add(stock);
                System.out.println(stock.toString());
            }
        }
        
        return stockSet;
    }
    private static void openBrowser(String url){
        // driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        driver.get(url);
        System.out.println(driver.getTitle());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        System.out.println("wait until contents are loaded.");
        wait.until(d -> driver.findElement(By.cssSelector(".tui-grid-cell-content")));
        System.out.println("contents are loaded. start crawling");
    }

    private static int scroll(List<WebElement> companyList, Set<Stock> stockSet, WebElement scrollArea)
            throws InterruptedException {
        int tryCount = 0;
        WebElement firstElement = companyList.get(0);
        int firstKey = Integer.parseInt(firstElement.getAttribute("data-row-key"));
        while (firstKey != stockSet.size() && tryCount < 50) {
            js.executeScript(
                    firstKey > stockSet.size() ? "arguments[0].scrollBy(0, -2);" : "arguments[0].scrollBy(0, 50);",
                    scrollArea);

            Thread.sleep(100);

            firstElement = driver.findElement(
                    By.xpath("//*[@id='jsGrid']/div/div[1]/div[1]/div[2]/div/div[1]/table/tbody/tr[1]/td"));
            firstKey = Integer.parseInt(firstElement.getAttribute("data-row-key"));

            System.out.println("tryCount : " + ++tryCount);
        }
        return tryCount;
    }

    // private static Set<Stock> contentToStock(List<WebElement> companyList, List<WebElement> infoList){

    // }
}
