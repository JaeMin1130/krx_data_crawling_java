package krx.crawling.model.repository;

import java.util.Set;

import jakarta.persistence.Query;
import krx.crawling.model.dao.StockDao;
import krx.crawling.utils.JPAUtil;

public class StockRepositoryImpl implements StockRepository {
    @Override
    public int insertCrawledStocks(Set<StockDao> stockSet) {
        int count = 0;
        
        for (StockDao stock : stockSet) {
            JPAUtil.inTransaction(entityManager -> {
                entityManager.persist(stock);
            });
            count++;
        }

        return count;
    }

    @Override
    public int upsertCrawledStocks(Set<StockDao> stockSet) {
        int[] totalCount = {0};

        JPAUtil.inTransaction(entityManager -> {
            // Define the SQL query for upsert operation
            String sql = "MERGE INTO stock AS target " +
             "USING (VALUES (:companyName, :marketCategory, :sector, :close, :tradingVolume, :tradingValue, :marketCap, :eps, :per, :bps, :pbr, :dps, :dy, :date)) AS source " +
             "(companyName, marketCategory, sector, close, tradingVolume, tradingValue, marketCap, eps, per, bps, pbr, dps, dy, date) " +
             "ON target.companyName = source.companyName AND target.date = source.date " +
             "WHEN MATCHED THEN UPDATE SET " +
             "marketCategory = source.marketCategory, " +
             "sector = source.sector, " +
             "close = source.close, " +
             "tradingVolume = source.tradingVolume, " +
             "tradingValue = source.tradingValue, " +
             "marketCap = source.marketCap, " +
             "eps = source.eps, " +
             "per = source.per, " +
             "bps = source.bps, " +
             "pbr = source.pbr, " +
             "dps = source.dps, " +
             "dy = source.dy " +
             "WHEN NOT MATCHED THEN INSERT (companyName, marketCategory, sector, close, tradingVolume, tradingValue, marketCap, eps, per, bps, pbr, dps, dy, date) " +
             "VALUES (source.companyName, source.marketCategory, source.sector, source.close, source.tradingVolume, source.tradingValue, source.marketCap, source.eps, source.per, source.bps, source.pbr, source.dps, source.dy, source.date)";

    
            // Loop through the stock set and execute the query for each stock
            for (StockDao stock : stockSet) {
                Query query = entityManager.createNativeQuery(sql);
                
                query.setParameter("companyName", stock.getCompanyName());
                query.setParameter("marketCategory", stock.getMarketCategory());
                query.setParameter("sector", stock.getSector());
                query.setParameter("close", stock.getClose());
                query.setParameter("tradingVolume", stock.getTradingVolume());
                query.setParameter("tradingValue", stock.getTradingValue());
                query.setParameter("marketCap", stock.getMarketCap());
                query.setParameter("eps", stock.getEps());
                query.setParameter("per", stock.getPer());
                query.setParameter("bps", stock.getBps());
                query.setParameter("pbr", stock.getPbr());
                query.setParameter("dps", stock.getDps());
                query.setParameter("dy", stock.getDy());
                query.setParameter("date", stock.getDate());
    
                totalCount[0] += query.executeUpdate();
            }
        });

        return totalCount[0];
    }

}