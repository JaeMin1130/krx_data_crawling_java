package krx.crawling.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Indicator {

    @Id
    @Column(nullable = false)
    private String companyName;
    private Double sma5;
    private Double sma20;
    private Double sma60;
    private Double per;
    private Double pbr;
    private Double dy;
    private LocalDate date;

}