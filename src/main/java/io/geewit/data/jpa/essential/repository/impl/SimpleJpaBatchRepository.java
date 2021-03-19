package io.geewit.data.jpa.essential.repository.impl;

import io.geewit.data.jpa.essential.repository.JpaBatchRepository;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量保存
 * @author geewit
 */
@Repository
public class SimpleJpaBatchRepository<T> implements JpaBatchRepository<T> {
    private final static Logger logger = LoggerFactory.getLogger(SimpleJpaBatchRepository.class);

    private final EntityManager entityManager;
    private final Session session;

    public SimpleJpaBatchRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.session = entityManager.unwrap(Session.class);
    }

    @Override
    public List<T> saveBatch(Iterable<T> entities) {
        List<T> result = new ArrayList<>();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            int i = 0;
            int batchSize = session.getJdbcBatchSize();
            for (T entity: entities) {
                if (i > 0 && (i % batchSize == 0)) {
                    logger.info("Flushing the EntityManager containing {} entities ...", session.getJdbcBatchSize());
                    entityTransaction.commit();
                    entityTransaction.begin();
                    entityManager.clear();
                }
                entityManager.persist(entity);
                result.add(entity);
                i++;
            }
            logger.info("Flushing the remaining entities ...");
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if (entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }

        return result;
    }
}
