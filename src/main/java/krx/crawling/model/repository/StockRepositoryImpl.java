package krx.crawling.model.repository;

import java.util.Set;

import jakarta.persistence.Query;
import krx.crawling.model.entity.Stock;
import krx.crawling.utils.JPAUtil;

public class StockRepositoryImpl implements StockRepository {
    @Override
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

    @Override
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

}