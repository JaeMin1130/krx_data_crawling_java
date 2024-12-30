package krx.crawling.model.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class FinanceStockDao {
    private String companyName;
    private String close;
    private String change;
    private String fluctuationRate;
    private String eps;
    private String per;
    private String bps;
    private String pbr;
    private String dps;
    private String dy;
}
