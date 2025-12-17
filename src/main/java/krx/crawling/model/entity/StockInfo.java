package krx.crawling.model.entity;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockInfo {
    private Long infoId;
    private Long stockCode;
    private LocalDate tradeDate;
    private Integer close;
    private Long tradingVolume;
    private Long tradingValue;
    private Long marketCap;
    private Integer eps;
    private Integer bps;
    private Integer dps;
}
