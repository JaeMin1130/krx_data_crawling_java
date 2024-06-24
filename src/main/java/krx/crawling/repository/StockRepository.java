package krx.crawling.repository;

import java.io.IOException;
import java.util.Set;

import krx.crawling.entity.Stock;

public interface StockRepository {
    void insertCrawledStocks(Set<Stock> stockSet) throws IOException;
}
