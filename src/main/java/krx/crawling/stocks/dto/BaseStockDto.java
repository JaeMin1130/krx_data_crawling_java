package krx.crawling.stocks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BaseStockDto {
    private String company;
    private String marketCategory;
    private String sector;
    private String close;
    private String volume;
    private String tradingValue;
    private String marketCap;
}
