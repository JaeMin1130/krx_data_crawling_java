package krx.crawling.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Entity
public class Stock implements Comparable<Stock> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "companyName", nullable = false)
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
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Override
    public int compareTo(Stock o) {
        return this.companyName.compareTo(o.getCompanyName());
    }
}
