package krx.crawling.domain.stocks.service;

import java.io.IOException;
import java.util.Set;

import krx.crawling.domain.stocks.entity.Stock;

public interface StockService {
    void insertCrawledStocks(Set<Stock> stockSet) throws IOException;
}
