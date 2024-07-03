package krx.crawling.stocks.entity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "stock", uniqueConstraints = @UniqueConstraint(columnNames = { "companyName", "date" }))
public class Stock implements Comparable<Stock> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String companyName;
    private String marketCategory;
    private String sector;
    private String close;
    private String tradingVolume;
    private String tradingValue;
    private String marketCap;
    private String eps;
    private String per;
    private String bps;
    private String pbr;
    private String dps;
    private String dy;

    @Column(nullable = false)
    @Convert(converter = LocalDateConverter.class)
    private LocalDate date;
    
    @Override
    public int compareTo(Stock o) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.companyName, o.companyName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Stock))
            return false;

        Stock stock = (Stock) o;
        return id == stock.id &&
                Objects.equals(companyName, stock.companyName) &&
                Objects.equals(marketCategory, stock.marketCategory) &&
                Objects.equals(sector, stock.sector) &&
                Objects.equals(close, stock.close) &&
                Objects.equals(tradingVolume, stock.tradingVolume) &&
                Objects.equals(tradingValue, stock.tradingValue) &&
                Objects.equals(marketCap, stock.marketCap) &&
                Objects.equals(eps, stock.eps) &&
                Objects.equals(per, stock.per) &&
                Objects.equals(bps, stock.bps) &&
                Objects.equals(pbr, stock.pbr) &&
                Objects.equals(dps, stock.dps) &&
                Objects.equals(dy, stock.dy) &&
                Objects.equals(date, stock.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyName, marketCategory, sector, close, tradingVolume, tradingValue, marketCap, eps, per, bps,
                pbr, dps, dy, date);
    }
}