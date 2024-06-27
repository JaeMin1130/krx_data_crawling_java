package krx.crawling.domain.stocks.entity;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "stock")  // Ensure this matches your actual table name
public class Stock implements Comparable<Stock> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Use the appropriate generation strategy
    private int id;
    
    private String company;
    private String marketCategory;
    private String sector;
    private String close;
    private String volume;
    private String tradingValue;
    private String marketCap;
    private String eps;
    private String pbr;
    private String per;
    private String bps;
    private String dps;
    private String dy;
    @Nonnull
    private LocalDateTime date;

    @Override
    public int compareTo(Stock o) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.company, o.company);
    }
}