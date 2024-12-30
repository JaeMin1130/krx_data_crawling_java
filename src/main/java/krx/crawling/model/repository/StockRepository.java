package krx.crawling.model.repository;

import java.util.Set;

import krx.crawling.model.dao.StockDao;

public interface StockRepository {
    int insertCrawledStocks(Set<StockDao> stockSet);
    int upsertCrawledStocks(Set<StockDao> stockSet);
    // void deleteOldestStock();
}
