package krx.crawling.domain.stocks.service;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import krx.crawling.domain.stocks.entity.Stock;
import krx.crawling.domain.stocks.entity.mapper.StockMapper;

public class StockServiceImpl implements StockService {

    private SqlSessionFactory sqlSessionFactory;

    public StockServiceImpl() throws IOException {
        try (Reader reader = Resources.getResourceAsReader("mybatis-config.xml")) {
            this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
    }

    @Override
    public void insertCrawledStocks(Set<Stock> stockSet) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            StockMapper mapper = session.getMapper(StockMapper.class);

            for (Stock stock : stockSet) {
                System.out.printf("%s is inserted into DB.%n", stock.getCompany());
                mapper.insertStock(stock);
            }

            session.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stock getStockById(int id) {
        Stock stock = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            StockMapper mapper = session.getMapper(StockMapper.class);

            stock = mapper.getStockById(id);

            session.commit();
            session.close();

            return stock;
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stock;
    }
}
