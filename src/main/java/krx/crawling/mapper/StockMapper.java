package krx.crawling.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

import krx.crawling.entity.Stock;

public interface StockMapper {

    @Insert("INSERT INTO stock (company, marketCategory, sector, close, volume, tradingValue, marketCap, eps, pbr, per, bps, dps, dy) " +
            "VALUES (#{company}, #{marketCategory}, #{sector}, #{close}, #{volume}, #{tradingValue}, #{marketCap}, #{eps}, #{pbr}, #{per}, #{bps}, #{dps}, #{dy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertStock(Stock stock);
}
