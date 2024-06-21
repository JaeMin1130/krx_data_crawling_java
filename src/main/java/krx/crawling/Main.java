package krx.crawling;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;
import java.util.TreeSet;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import krx.crawling.entity.Stock;
import krx.crawling.mapper.StockMapper;
import krx.crawling.utils.FileWriterUtil;
import krx.crawling.utils.KrxCrawler;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        Set<Stock> stockSet = new TreeSet<>();
        stockSet = KrxCrawler.execute();
        try (Reader reader = Resources.getResourceAsReader("mybatis-config.xml")) {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            try (SqlSession session = sqlSessionFactory.openSession()) {
                StockMapper mapper = session.getMapper(StockMapper.class);

                for (Stock stock : stockSet) {
                    System.out.printf("%s is inserted into DB.", stock.getCompany());
                    mapper.insertStock(stock);
                }

                session.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Save the extracted text to a file
        String filePath = "./app/stocks.txt";

        FileWriterUtil.writeStocksToFile(stockSet, filePath);
    }
}
