package krx.crawling.domain.stocks.entity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import krx.crawling.domain.stocks.entity.Stock;

public interface StockMapper {

    @Insert("INSERT INTO stock (company, marketCategory, sector, close, volume, tradingValue, marketCap, eps, pbr, per, bps, dps, dy) " +
            "VALUES (#{company}, #{marketCategory}, #{sector}, #{close}, #{volume}, #{tradingValue}, #{marketCap}, #{eps}, #{pbr}, #{per}, #{bps}, #{dps}, #{dy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertStock(Stock stock);

    @Select("SELECT * FROM stock WHERE id = #{id}")
    Stock getStockById(int id);

    @Select("SELECT * FROM stock")
    List<Stock> getAllStocks();

    @Update("UPDATE stock SET company = #{company}, marketCategory = #{marketCategory}, sector = #{sector}, close = #{close}, volume = #{volume}, " +
            "tradingValue = #{tradingValue}, marketCap = #{marketCap}, eps = #{eps}, pbr = #{pbr}, per = #{per}, bps = #{bps}, dps = #{dps}, dy = #{dy} " +
            "WHERE id = #{id}")
    void updateStock(Stock stock);

    @Delete("DELETE FROM stock WHERE id = #{id}")
    void deleteStock(int id);
}
