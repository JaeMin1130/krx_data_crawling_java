package krx.crawling.stocks.repository;

import java.util.Set;

import krx.crawling.stocks.entity.Stock;

// Stock to DAO, JPA
public interface StockRepository {
    void insertCrawledStocks(Set<Stock> stockSet);
}
