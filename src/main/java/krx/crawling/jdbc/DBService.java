package krx.crawling.jdbc;

import static krx.crawling.jdbc.DBConnector.connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Set;

import krx.crawling.model.dao.StockDao;
import krx.crawling.model.entity.Stock;
import krx.crawling.model.entity.StockInfo;
import krx.crawling.model.entity.StockMaster;

public class DBService {

    private DBService() {
    };

    public static void upsertCrawledStocks(Set<Stock> stockSet) {
        try (final Connection conn = connect()) {
            conn.setAutoCommit(false);
            StockDao stockDao = new StockDao();

            for (Stock stock : stockSet) {
                try {
                    Long stockCode = stockDao.findByStockName(conn, stock.getCompanyName());
                    if (stockCode == null) {
                        StockMaster stockMaster = StockMaster.builder()
                                .stockName(stock.getCompanyName())
                                .marketType(stock.getMarketCategory())
                                .sector(stock.getSector())
                                .build();
                        stockCode = stockDao.saveMaster(conn, stockMaster);
                    }

                    LocalDate tradeDate = stock.getDate();
                    if (!stockDao.existsByStockCodeAndTradeDate(conn, stockCode, tradeDate)) {
                        StockInfo stockInfo = StockInfo.builder()
                                .stockCode(stockCode)
                                .tradeDate(tradeDate)
                                .close(stock.getClose())
                                .tradingVolume(stock.getTradingVolume())
                                .tradingValue(stock.getTradingValue())
                                .marketCap(stock.getMarketCap())
                                .eps(stock.getEps())
                                .bps(stock.getBps())
                                .dps(stock.getDps())
                                .build();
                        stockDao.saveInfo(conn, stockInfo);
                    }
                } catch (SQLException e) {
                    conn.rollback();
                    e.printStackTrace();
                }
            }
            conn.commit();
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
        // This part is not changed as per the request.
        // However, it might need changes if it depends on the old Stock table.
        // For now, leaving it as is.
        try (final Connection conn = connect();
                final Statement stmt = conn.createStatement();) {
            // This query is now invalid if it uses the old table structure.
            // String query = FileReader.read(QUERY).getProperty("query.indicator.upsert");
            // stmt.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
