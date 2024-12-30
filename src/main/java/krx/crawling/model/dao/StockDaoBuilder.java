package krx.crawling.model.dao;

import java.util.List;

@FunctionalInterface
public interface StockDaoBuilder<T> {
    T build(List<String> values);
}

