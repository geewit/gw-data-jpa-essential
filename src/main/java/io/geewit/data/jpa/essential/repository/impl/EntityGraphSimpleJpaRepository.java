package io.geewit.data.jpa.essential.repository.impl;

import io.geewit.data.jpa.essential.domain.EntityGraph;
import io.geewit.data.jpa.essential.repository.EntityGraphJpaRepository;
import io.geewit.data.jpa.essential.repository.EntityGraphJpaSpecificationExecutor;
import io.geewit.data.jpa.essential.repository.JpaBatchExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * A {@link SimpleJpaRepository} that supports {@link EntityGraph} passed through method arguments.
 *
 * <p>Created on 22/11/16.
 *
 * @author Reda.Housni-Alaoui
 */
public class EntityGraphSimpleJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements EntityGraphJpaRepository<T, ID>, EntityGraphJpaSpecificationExecutor<T>, JpaBatchExecutor<T> {

    private static final Logger logger = LoggerFactory.getLogger(EntityGraphSimpleJpaRepository.class);

    public EntityGraphSimpleJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        logger.debug("BATCH_SIZE = {}", BATCH_SIZE);
    }

    public EntityGraphSimpleJpaRepository(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
        logger.debug("BATCH_SIZE = {}", BATCH_SIZE);
    }

    private final EntityManager entityManager;

    private static final Integer BATCH_SIZE = 500;

    @Override
    public Optional<T> findOne(Specification<T> spec, EntityGraph entityGraph) {
        return super.findOne(spec);
    }

    @Override
    public List<T> findAll(Specification<T> spec, EntityGraph entityGraph) {
        return super.findAll(spec);
    }

    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable, EntityGraph entityGraph) {
        return super.findAll(spec, pageable);
    }

    @Override
    public List<T> findAll(Specification<T> spec, Sort sort, EntityGraph entityGraph) {
        return super.findAll(spec, sort);
    }

    @Override
    public Optional<T> findById(ID id, EntityGraph entityGraph) {
        return super.findById(id);
    }

    @Override
    public Page<T> findAll(Pageable pageable, EntityGraph entityGraph) {
        return super.findAll(pageable);
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids, EntityGraph entityGraph) {
        return super.findAllById(ids);
    }

    @Override
    public Iterable<T> findAll(Sort sort, EntityGraph entityGraph) {
        return super.findAll(sort);
    }

    @Override
    public Iterable<T> findAll(EntityGraph entityGraph) {
        return super.findAll();
    }

    @Transactional
    @Override
    public List<T> saveBatch(Iterable<T> entities) {
        List<T> result = new ArrayList<>();
        int i = 0;
        for (Iterator<T> iterator = entities.iterator(); iterator.hasNext(); ) {
            i++;
            T entity = iterator.next();
            if (entity != null) {
                entityManager.persist(entity);
            }
            if (i % BATCH_SIZE == 0 || !iterator.hasNext()) {
                logger.info("Flushing the EntityManager containing {} entities ...", i);
                if (entity != null) {
                    entityManager.flush();
                    entityManager.clear();
                }
                i = 0;
            }
            if (entity != null) {
                result.add(entity);
            }
        }
        logger.info("Flushing the remaining entities ...");
        return result;
    }
}
