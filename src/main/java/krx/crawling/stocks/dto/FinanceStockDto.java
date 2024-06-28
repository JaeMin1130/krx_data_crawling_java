package krx.crawling.stocks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class FinanceStockDto {
    private String company;
    private String close;
    private String eps;
    private String per;
    private String bps;
    private String pbr;
    private String dps;
    private String dy;
}
