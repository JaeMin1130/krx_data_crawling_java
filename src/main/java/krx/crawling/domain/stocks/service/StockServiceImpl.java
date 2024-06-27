package krx.crawling.domain.stocks.service;

import java.util.Set;

import krx.crawling.domain.stocks.entity.Stock;
import krx.crawling.utils.JPAUtil;

public class StockServiceImpl implements StockService {

    @Override
    public void insertCrawledStocks(Set<Stock> stockSet) {
        for(Stock stock : stockSet){
            JPAUtil.inTransaction(entityManager -> {
                entityManager.persist(stock);
            });
        }
    }
}
