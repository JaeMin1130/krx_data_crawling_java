package krx.crawling.stocks.repository;

import java.util.Set;

import krx.crawling.stocks.entity.Stock;

public interface StockRepository {
    void insertCrawledStocks(Set<Stock> stockSet);
    // void deleteOldestStock();
}
