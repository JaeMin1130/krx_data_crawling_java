package krx.crawling.domain.stocks.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public final class Stock implements Comparable<Stock> {
    private final int id;
    private final String company;         // 종목명
    private final String marketCategory;  // 시장구분
    private final String sector;          // 소속부
    private final String close;           // 종가
    private final String volume;          // 거래량
    private final String tradingValue;    // 거래대금
    private final String marketCap;       // 시가총액
    private final String eps;             // Earnings Per Share(주당순이익)
    private final String pbr;             // Price-to-Book Ratio(주가순자산비율)
    private final String per;             // Price-to-Earnings Ratio(주가수익비율)
    private final String bps;             // Book Value Per Share(주당순자산가치)
    private final String dps;             // Dividend Per Share(주당배당금)
    private final String dy;              // Dividend Yield(배당수익률)

    @Override
    public int compareTo(Stock o) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.company, o.company);
    }

}
