package krx.crawling;

import java.io.IOException;
import java.util.Set;

import krx.crawling.domain.stocks.entity.Stock;
import krx.crawling.domain.stocks.service.StockService;
import krx.crawling.domain.stocks.service.StockServiceImpl;
import krx.crawling.utils.KrxCrawler;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        Set<Stock> stockSet = KrxCrawler.execute();
        
        StockService stockService= new StockServiceImpl();
        stockService.insertCrawledStocks(stockSet);
                
        
        // JPAUtil.inTransaction(entityManager -> {
        //     entityManager.find(Stock.class, 2);
        //     entityManager.createQuery("select e from Stock e", Stock.class).getResultList()
        //             .forEach(stock -> out.println(stock.toString()));
        // });


        // // WebhookService webhookService = new WebhookServiceImpl();
        // // String url =
        // "https://discordapp.com/api/webhooks/1255033445575426070/-ej_99Y6ac8bHD5PK8FJc8_-9KStaTg0MT_cUoWFe_juDl_ovArTx5cZ5WEN0YP50oOO";
        // // String json = "";
        // // webhookService.sendPostRequest(url, json);

    }
}
