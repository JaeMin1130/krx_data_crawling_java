package krx.crawling.utils;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

import java.util.function.Consumer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class JPAUtil {
    // an EntityManagerFactory is set up once for an application
    // IMPORTANT: notice how the name here matches the name we
    // gave the persistence-unit in persistence.xml
    private static final EntityManagerFactory entityManagerFactory = createEntityManagerFactory("org.hibernate.stock.jpa");

    public static void inTransaction(Consumer<EntityManager> work) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            work.accept(entityManager);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }
}
