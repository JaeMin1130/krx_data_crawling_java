package krx.crawling;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import krx.crawling.domain.stocks.entity.Stock;
import krx.crawling.domain.stocks.service.StockService;
import krx.crawling.domain.stocks.service.StockServiceImpl;
import krx.crawling.domain.webhooks.service.WebhookService;
import krx.crawling.domain.webhooks.service.WebhookServiceImpl;
import krx.crawling.utils.KrxCrawler;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        Set<Stock> stockSet = new TreeSet<>();
        stockSet = KrxCrawler.execute();

        // Use StockRepository to handle database operations
        StockService stockService = new StockServiceImpl();
        stockService.insertCrawledStocks(stockSet);

        // Stock stock = stockService.getStockById(1);
        // WebhookService webhookService = new WebhookServiceImpl();
        // String url = "https://discordapp.com/api/webhooks/1255033445575426070/-ej_99Y6ac8bHD5PK8FJc8_-9KStaTg0MT_cUoWFe_juDl_ovArTx5cZ5WEN0YP50oOO";
        // String json = "";
        // webhookService.sendPostRequest(url, json);

        // Save the extracted text to a file
        String filePath = "./app/stocks.txt";

        // FileWriterUtil.writeStocksToFile(stockSet, filePath);
    }
}
