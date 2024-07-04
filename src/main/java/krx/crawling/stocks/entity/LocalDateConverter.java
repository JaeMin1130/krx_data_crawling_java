package krx.crawling.stocks.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {
 
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
 
    @Override
    public String convertToDatabaseColumn(LocalDate localDate) {
        return (localDate == null ? null : localDate.format(formatter));
    }
 
    @Override
    public LocalDate convertToEntityAttribute(String sqlDate) {
        return (sqlDate == null ? null : LocalDate.parse(sqlDate, formatter));
    }
}
