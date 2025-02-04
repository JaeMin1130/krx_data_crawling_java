package krx.crawling.jdbc;

import static krx.crawling.file.FilePath.QUERY;
import static krx.crawling.jdbc.DBConnector.connect;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Set;

import krx.crawling.file.FileReader;
import krx.crawling.model.entity.Stock;

public class DBService {

    private DBService() {
    };

    public static void upsertCrawledStocks(Set<Stock> stockSet) {
        try (final Connection conn = connect();
                final PreparedStatement ps = conn
                        .prepareStatement(FileReader.read(QUERY).getProperty("query.stock.upsert"))) {

            for (Stock stock : stockSet) {
                
                ps.setString(1, stock.getCompanyName());
                ps.setString(2, stock.getMarketCategory());
                ps.setString(3, stock.getSector());
                ps.setInt(4, stock.getClose());
                ps.setLong(5, stock.getTradingVolume());
                ps.setLong(6, stock.getTradingValue());
                ps.setLong(7, stock.getMarketCap());
                setNullableInt(ps, 8, stock.getEps());
                setNullableInt(ps, 9, stock.getBps());
                setNullableInt(ps, 10, stock.getDps());
                ps.setDate(11, Date.valueOf(stock.getDate()));

                ps.addBatch();
            }

            ps.executeBatch(); 
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) 
            ps.setNull(index, Types.INTEGER);
        else 
            ps.setInt(index, value);
    }
    
    public static void upsertIndicator() {
        
        try (final Connection conn = connect();
        final Statement stmt = conn.createStatement();) {
            stmt.execute(FileReader.read(QUERY).getProperty("query.indicator.upsert"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
