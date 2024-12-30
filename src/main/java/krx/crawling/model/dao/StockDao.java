package krx.crawling.model.dao;


import jakarta.persistence.Column;
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
@Table(name = "stock", uniqueConstraints = @UniqueConstraint(columnNames = {"companyName", "date"}))
public class StockDao implements Comparable<StockDao> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "companyName", nullable = false)
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

    @Column(name = "date", nullable = false)
    private String date;

    @Override
    public int compareTo(StockDao o) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.companyName, o.companyName);
    }
}