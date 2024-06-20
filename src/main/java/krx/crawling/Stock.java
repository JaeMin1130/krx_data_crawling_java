package krx.crawling;

import static java.util.Comparator.comparing;

import java.util.Comparator;
import java.util.List;

import org.openqa.selenium.WebElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
final class Stock implements Comparable<Stock>{
    private final int id;
    private final String company;           // 종목명
    private final String marketCategory;    // 시장구분
    private final String sector;            // 소속부
    private final String close;             // 종가
    private final String volume;            // 거래량
    private final String tradingValue;      // 거래대금
    private final String marketCap;         // 시가총액
    private final String eps;               // Earnings Per Share(주당순이익)
    private final String pbr;               // Price-to-Earnings Ratio(주가수익비율)
    private final String per;               // Book Value Per Share(주당순자산가치)
    private final String bps;               // Price-to-Book Ratio(주가순자산비율)
    private final String dps;               // Dividend Per Share(주당배당금)
    private final String dy;                // Dividend Yield(배당수익률)

    private static final Comparator<Stock> COMPARATOR = 
    comparing((Stock st) -> st.company);
    // .thenComparingInt(st ->  st.prefix)
    // .thenComparingInt(st -> st.lineNum);
    
    @Override
    public int compareTo(Stock st){
        return COMPARATOR.compare(this, st);
    }
    
    // @Override
    // public int compareTo(Stock o) {
        //     return String.CASE_INSENSITIVE_ORDER.compare(this.company, o.company);
        // }

}
