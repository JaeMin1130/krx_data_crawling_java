package krx.crawling.repository;

import java.util.Set;

import jakarta.persistence.Query;
import krx.crawling.model.entity.Stock;

public class RepositoryService {
    public int insertCrawledStocks(Set<Stock> stockSet) {
        int count = 0;

        for (Stock stock : stockSet) {
            JPAUtil.inTransaction(entityManager -> {
                entityManager.persist(stock);
            });
            count++;
        }

        return count;
    }

    public int upsertCrawledStocks(Set<Stock> stockSet) {
        int[] totalCount = { 0 };

        JPAUtil.inTransaction(entityManager -> {
            // Define the SQL query for the upsert operation
            String sql = "MERGE INTO stock AS target " +
                    "USING (VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)) AS source " +
                    "(companyName, marketCategory, sector, close, tradingVolume, tradingValue, marketCap, eps, bps, dps, date) "
                    +
                    "ON (target.companyName = source.companyName AND target.date = source.date) " +
                    "WHEN MATCHED THEN UPDATE SET " +
                    "target.marketCategory = source.marketCategory, " +
                    "target.sector = source.sector, " +
                    "target.close = source.close, " +
                    "target.tradingVolume = source.tradingVolume, " +
                    "target.tradingValue = source.tradingValue, " +
                    "target.marketCap = source.marketCap, " +
                    "target.eps = source.eps, " +
                    "target.bps = source.bps, " +
                    "target.dps = source.dps " +
                    "WHEN NOT MATCHED THEN INSERT (companyName, marketCategory, sector, close, tradingVolume, tradingValue, marketCap, eps, bps, dps, date) "
                    +
                    "VALUES (source.companyName, source.marketCategory, source.sector, source.close, source.tradingVolume, source.tradingValue, source.marketCap, source.eps, source.bps, source.dps, source.date)";

            // Loop through the stock set and execute the query for each stock
            for (Stock stock : stockSet) {
                Query query = entityManager.createNativeQuery(sql);

                // Set positional parameters for the SQL query
                query.setParameter(1, stock.getCompanyName());
                query.setParameter(2, stock.getMarketCategory());
                query.setParameter(3, stock.getSector());
                query.setParameter(4, stock.getClose());
                query.setParameter(5, stock.getTradingVolume());
                query.setParameter(6, stock.getTradingValue());
                query.setParameter(7, stock.getMarketCap());
                query.setParameter(8, stock.getEps());
                query.setParameter(9, stock.getBps());
                query.setParameter(10, stock.getDps());
                query.setParameter(11, stock.getDate());

                totalCount[0] += query.executeUpdate();
            }
        });

        return totalCount[0];
    }

    public void upsertIndicator() {
        JPAUtil.inTransaction(entityManager -> {
            String sql = "MERGE INTO indicator AS target\r\n" + //
                    "\tUSING (SELECT * FROM indicator_value) AS source (companyname, sma5, sma20, sma60, per, pbr, dy, date)\r\n"
                    + //
                    "\t\tON (target.companyname = source.companyname)\r\n" + //
                    "\t\t\tWHEN MATCHED THEN\r\n" + //
                    "\t\t\t    UPDATE SET\r\n" + //
                    "\t\t\t        target.sma5 = source.sma5,\r\n" + //
                    "\t\t\t        target.sma20 = source.sma20,\r\n" + //
                    "\t\t\t        target.sma60 = source.sma60,\r\n" + //
                    "\t\t\t        target.per = source.per,\r\n" + //
                    "\t\t\t        target.pbr = source.pbr,\r\n" + //
                    "\t\t\t        target.dy = source.dy,\r\n" + //
                    "\t\t\t        target.date = source.date\r\n" + //
                    "\t\t\tWHEN NOT MATCHED THEN \r\n" + //
                    "\t\t\t    INSERT (companyname, sma5, sma20, sma60, per, pbr, dy, date)\r\n" + //
                    "\t\t\t    VALUES (source.companyname, source.sma5, source.sma20, source.sma60, source.per, source.pbr, source.dy, source.date)";

            // Loop through the stock set and execute the query for each stock
            Query query = entityManager.createNativeQuery(sql);

            query.executeUpdate();
        });
    }

}