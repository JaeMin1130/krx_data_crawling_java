package krx.crawling.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import krx.crawling.model.entity.StockInfo;
import krx.crawling.model.entity.StockMaster;

public class StockDao {

    public Long findByStockName(Connection conn, String stockName) throws SQLException {
        String sql = "SELECT stock_code FROM stock_master WHERE stock_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stockName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("stock_code");
                }
            }
        }
        return null;
    }

    public long saveMaster(Connection conn, StockMaster stockMaster) throws SQLException {
        String sql = "INSERT INTO stock_master (stock_name, market_type, sector) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, stockMaster.getStockName());
            ps.setString(2, stockMaster.getMarketType());
            ps.setString(3, stockMaster.getSector());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating stock master failed, no ID obtained.");
                }
            }
        }
    }

    public boolean existsByStockCodeAndTradeDate(Connection conn, Long stockCode, java.time.LocalDate tradeDate) throws SQLException {
        String sql = "SELECT 1 FROM stock_info WHERE stock_code = ? AND trade_date = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, stockCode);
            ps.setDate(2, Date.valueOf(tradeDate));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void saveInfo(Connection conn, StockInfo stockInfo) throws SQLException {
        String sql = "INSERT INTO stock_info (stock_code, trade_date, close, trading_volume, trading_value, market_cap, eps, bps, dps) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, stockInfo.getStockCode());
            ps.setDate(2, Date.valueOf(stockInfo.getTradeDate()));
            ps.setInt(3, stockInfo.getClose());
            ps.setLong(4, stockInfo.getTradingVolume());
            ps.setLong(5, stockInfo.getTradingValue());
            ps.setLong(6, stockInfo.getMarketCap());
            setNullableInt(ps, 7, stockInfo.getEps());
            setNullableInt(ps, 8, stockInfo.getBps());
            setNullableInt(ps, 9, stockInfo.getDps());
            ps.executeUpdate();
        }
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }
}