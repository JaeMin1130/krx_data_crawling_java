package krx.crawling;

import static java.util.Comparator.*;

import java.util.Comparator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
final class Stock implements Comparable<Stock>{
    private final int id;
    private final String company;
    private final String marketCategory;
    private final String sector;
    private final String close;
    private final String volume;
    private final String tradingValue;
    private final String marketCap;

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
