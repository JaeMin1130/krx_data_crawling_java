package krx.crawling.model.entity;

import java.time.LocalDate;

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
public class Stock implements Comparable<Stock> {
    private Integer id;
    private String companyName;
    private String marketCategory;
    private String sector;
    private Integer close;
    private Long tradingVolume;
    private Long tradingValue;
    private Long marketCap;
    private Integer eps;
    private Integer bps;
    private Integer dps;
    // private double per;
    // private double pbr;
    // private double dy;
    private LocalDate date;

    @Override
    public int compareTo(Stock o) {
        return this.companyName.compareTo(o.getCompanyName());
    }
}
