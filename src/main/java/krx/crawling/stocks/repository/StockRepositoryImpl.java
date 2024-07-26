package krx.crawling.stocks.repository;

import java.util.Set;

import jakarta.persistence.Query;
import krx.crawling.stocks.entity.Stock;
import krx.crawling.utils.JPAUtil;

public class StockRepositoryImpl implements StockRepository {
    @Override
    public void insertCrawledStocks(Set<Stock> stockSet) {
        for (Stock stock : stockSet) {
            JPAUtil.inTransaction(entityManager -> {
                entityManager.persist(stock);
            });
        }
    }

    @Override
    public int upsertCrawledStocks(Set<Stock> stockSet) {
        int[] totalCount = {0};

        JPAUtil.inTransaction(entityManager -> {
            // Define the SQL query for upsert operation
            String sql = "INSERT INTO stock (companyName, marketCategory, sector, close, tradingVolume, tradingValue, marketCap, eps, per, bps, pbr, dps, dy, date) " +
                         "VALUES (:companyName, :marketCategory, :sector, :close, :tradingVolume, :tradingValue, :marketCap, :eps, :per, :bps, :pbr, :dps, :dy, :date) " +
                         "ON CONFLICT (companyName, date) DO UPDATE SET " +
                         "marketCategory = excluded.marketCategory, " +
                         "sector = excluded.sector, " +
                         "close = excluded.close, " +
                         "tradingVolume = excluded.tradingVolume, " +
                         "tradingValue = excluded.tradingValue, " +
                         "marketCap = excluded.marketCap, " +
                         "eps = excluded.eps, " +
                         "per = excluded.per, " +
                         "bps = excluded.bps, " +
                         "pbr = excluded.pbr, " +
                         "dps = excluded.dps, " +
                         "dy = excluded.dy";
    
            // Loop through the stock set and execute the query for each stock
            for (Stock stock : stockSet) {
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
