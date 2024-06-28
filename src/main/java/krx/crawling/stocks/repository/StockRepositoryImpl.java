package krx.crawling.stocks.repository;

import java.util.Set;

import krx.crawling.stocks.entity.Stock;
import krx.crawling.utils.JPAUtil;

public class StockRepositoryImpl implements StockRepository {
    @Override
    public void insertCrawledStocks(Set<Stock> stockSet) {
        for (Stock stock : stockSet) {
            JPAUtil.inTransaction(entityManager -> {
                entityManager.persist(stock);
            });
        }
    }
}
