package krx.crawling.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockDao {
    private int id;
    private String companyName;
    private String marketCategory;
    private String sector;
    private String close;
    private String tradingVolume;
    private String tradingValue;
    private String marketCap;
    private String eps;
    private String per;
    private String bps;
    private String pbr;
    private String dps;
    private String dy;
    private String date;
}