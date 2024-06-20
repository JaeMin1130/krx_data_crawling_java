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

public final class CrawlerTemp {
    // private final Crawler crawler = new Crawler();
    private static WebDriver driver;
    private static JavascriptExecutor js;

    private CrawlerTemp() {
        throw new AssertionError();
    }

    static Set<Stock> execute() throws InterruptedException {
        startChromeDriver();
        
        List<WebElement> list = crawlContents("http://data.krx.co.kr/contents/MMC/ISIF/isif/MMCISIF001.cmd");
        System.out.println("----------------------------------------------Finish first crawling----------------------------------------------");
        list.addAll(crawlContents("http://data.krx.co.kr/contents/MMC/ISIF/isif/MMCISIF002.cmd"));
        System.out.println(list.size());

        Set<Stock> stockSet = new TreeSet<>();

        return stockSet;
    }

    private static void startChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run Chrome in headless mode(no UI)
        options.addArguments("--no-sandbox"); // Bypass OS security model

        driver = new ChromeDriver(options);
        js = (JavascriptExecutor) driver;
    }

    private static List<WebElement> crawlContents(String url) throws InterruptedException {

        openBrowser(url);

        List<WebElement> bodyArea = driver.findElements(By.cssSelector(".tui-grid-body-area"));
        WebElement nameArea = bodyArea.get(0);
        WebElement infoArea = bodyArea.get(1);

        List<WebElement> companyList = nameArea.findElements(By.cssSelector(".tui-grid-cell"));
        List<WebElement> infoList = infoArea
                .findElements(By.cssSelector(".tui-grid-row-odd, .tui-grid-row-even"));
        System.out.println(infoList.size());
        int tryCount = 0;
        int listSize = infoList.size();
        while (tryCount < 50) {
            tryCount = scroll(companyList, listSize, infoArea);

            companyList = nameArea.findElements(By.cssSelector(".tui-grid-cell"));
            infoList.addAll(infoArea.findElements(By.cssSelector(".tui-grid-row-odd, .tui-grid-row-even")));

            listSize = infoList.size();
            System.out.println(listSize);
            System.out.printf("%.2f%% done%n", (double) listSize / 2800 * 100);
        }

        return infoList;
    }

    private static void openBrowser(String url) {
        // driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        driver.get(url);
        System.out.println(driver.getTitle());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        System.out.println("wait until contents are loaded.");
        wait.until(d -> driver.findElement(By.cssSelector(".tui-grid-cell-content")));
        System.out.println("contents are loaded. start crawling");
    }

    private static int scroll(List<WebElement> companyList, int listSize, WebElement infoArea)
            throws InterruptedException {
        int tryCount = 0;
        WebElement firstElement = companyList.get(0);
        int firstKey = Integer.parseInt(firstElement.getAttribute("data-row-key"));
        while (firstKey != listSize && tryCount < 50) {
            js.executeScript(
                    firstKey > listSize ? "arguments[0].scrollBy(0, -2);" : "arguments[0].scrollBy(0, 50);",
                    infoArea);

            Thread.sleep(100);

            firstElement = driver.findElement(
                    By.xpath("//*[@id='jsGrid']/div/div[1]/div[1]/div[2]/div/div[1]/table/tbody/tr[1]/td"));
            firstKey = Integer.parseInt(firstElement.getAttribute("data-row-key"));

            System.out.println("tryCount : " + ++tryCount);
        }
        return tryCount;
    }
}
