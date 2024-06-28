package krx.crawling;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Set;
import static java.time.LocalDateTime.now;
import krx.crawling.stocks.entity.Stock;
import krx.crawling.stocks.repository.StockRepository;
import krx.crawling.stocks.repository.StockRepositoryImpl;
import krx.crawling.utils.KrxCrawler;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        Set<Stock> stockSet = KrxCrawler.execute("2023-06-28");
        
        StockRepository stockRepo = new StockRepositoryImpl();
        stockRepo.insertCrawledStocks(stockSet);
                
        // // WebhookService webhookService = new WebhookServiceImpl();
        // // String url =
        // "https://discordapp.com/api/webhooks/1255033445575426070/-ej_99Y6ac8bHD5PK8FJc8_-9KStaTg0MT_cUoWFe_juDl_ovArTx5cZ5WEN0YP50oOO";
        // // String json = "";
        // // webhookService.sendPostRequest(url, json);

    }
}
