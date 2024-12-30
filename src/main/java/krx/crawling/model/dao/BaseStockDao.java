package krx.crawling.model.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class BaseStockDao {
    private String companyName;
    private String marketCategory;
    private String sector;
    private String close;
    private String change;
    private String fluctuationRate;
    private String tradingVolume;
    private String tradingValue;
    private String marketCap;
}
