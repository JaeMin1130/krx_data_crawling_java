package krx.crawling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

public class FirstScriptCopy {

    public static void main(String[] args) throws InterruptedException, IOException {
        // Set ChromeOptions for headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run Chrome in headless mode(no UI)
        options.addArguments("--no-sandbox"); // Bypass OS security model

        WebDriver driver = new ChromeDriver(options);

        driver.get("http://data.krx.co.kr/contents/MMC/ISIF/isif/MMCISIF001.cmd");
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        System.out.println(driver.getTitle());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(d -> driver.findElement(By.cssSelector(".tui-grid-cell-content")));

        List<WebElement> bodyArea = driver.findElements(By.cssSelector(".tui-grid-body-area"));
        WebElement nameArea = bodyArea.get(0);
        WebElement scrollArea = bodyArea.get(1);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<WebElement> companyList = nameArea.findElements(By.cssSelector(".tui-grid-cell"));
        List<WebElement> infoList = scrollArea.findElements(By.cssSelector(".tui-grid-row-odd, .tui-grid-row-even"));
        Set<Stock> stockSet = new TreeSet<>();

        int tryCount = 0;
        while (tryCount < 1) {
            tryCount = 0;
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

        // Save the extracted text to a file
        String filePath = "./app/stocks.txt";

        // Ensure the directory exists before writing the file
        File file = new File(filePath);
        file.getParentFile().mkdirs(); // Create directories if they don't exist

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (Stock stock : stockSet) {
                out.println(stock.toString());
            }
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }

        driver.quit();
    }
}
