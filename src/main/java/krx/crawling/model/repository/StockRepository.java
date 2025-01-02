package krx.crawling.model.repository;

import java.util.Set;

import krx.crawling.model.entity.Stock;

public interface StockRepository {
    int insertCrawledStocks(Set<Stock> stockSet);
    int upsertCrawledStocks(Set<Stock> stockSet);
    // void deleteOldestStock();
}
