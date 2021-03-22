package io.geewit.data.jpa.essential.repository.impl;

import io.geewit.data.jpa.essential.repository.JpaBatchRepository;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 批量保存
 * @author geewit
 */
public class SimpleJpaBatchRepository<T, ID> extends SimpleJpaRepository<T, ID> implements JpaBatchRepository<T, ID> {
    private final static Logger logger = LoggerFactory.getLogger(SimpleJpaBatchRepository.class);

    private final EntityManager entityManager;

    private final Integer batchSize;

    public SimpleJpaBatchRepository(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        Object batchSizeObj = entityManager.getProperties().get(AvailableSettings.STATEMENT_BATCH_SIZE);
        this.batchSize = (Integer)batchSizeObj;
    }

    @Transactional
    @Override
    public List<T> saveBatch(Iterable<T> entities) {
        List<T> result = new ArrayList<>();
        int i = 0;
        for (Iterator<T> iterator = entities.iterator(); iterator.hasNext(); ) {
            T entity = iterator.next();
            entityManager.persist(entity);
            i++;
            if (i % this.batchSize == 0 || !iterator.hasNext()) {
                logger.info("Flushing the EntityManager containing {} entities ...", i);
                entityManager.flush();
                entityManager.clear();
                i = 0;
            }
            result.add(entity);
        }
        logger.info("Flushing the remaining entities ...");
        return result;
    }
}
