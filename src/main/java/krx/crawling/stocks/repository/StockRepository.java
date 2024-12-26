package krx.crawling.stocks.repository;

import java.util.Set;

import krx.crawling.stocks.entity.Stock;

public interface StockRepository {
    int insertCrawledStocks(Set<Stock> stockSet);
    int upsertCrawledStocks(Set<Stock> stockSet);
    // void deleteOldestStock();
}
