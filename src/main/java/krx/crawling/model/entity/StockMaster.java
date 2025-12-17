package krx.crawling.model.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockMaster {
    private Long stockCode;
    private String stockName;
    private String marketType;
    private String sector;
}
