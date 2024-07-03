package krx.crawling.stocks.dto;

import java.util.List;

@FunctionalInterface
public interface StockDtoBuilder<T> {
    T build(List<String> values);
}

