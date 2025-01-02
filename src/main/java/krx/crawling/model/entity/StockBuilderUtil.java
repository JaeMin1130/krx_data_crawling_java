package krx.crawling.model.entity;

public class StockBuilderUtil {
    public static Integer parseInteger(String value) {
        return (value == null || value.equals("-")) ? null : Integer.parseInt(value.replace(",", ""));
    }

    public static Long parseLong(String value) {
        return (value == null || value.equals("-")) ? null : Long.parseLong(value.replace(",", ""));
    }

    public static Double parseDouble(String value) {
        return (value == null || value.equals("-")) ? null : Double.parseDouble(value.replace(",", ""));
    }
}

