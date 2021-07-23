package io.geewit.data.jpa.essential.repository.support;

import io.geewit.data.jpa.essential.domain.EntityGraph;
import io.geewit.data.jpa.essential.repository.impl.EntityGraphSimpleJpaRepository;
import io.geewit.data.jpa.essential.repository.query.EntityGraphAwareJpaQueryMethodFactory;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;

/**
 * This repository factory allows to build {@link EntityGraph} aware repositories. Created on
 * 22/11/16.
 *
 * @author Reda.Housni-Alaoui
 */
public class EntityGraphJpaRepositoryFactory extends JpaRepositoryFactory {

    /**
     * Creates a new {@link JpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public EntityGraphJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        super.addRepositoryProxyPostProcessor(new RepositoryMethodEntityGraphExtractor(entityManager));
        super.setQueryMethodFactory(
                new EntityGraphAwareJpaQueryMethodFactory(
                        PersistenceProvider.fromEntityManager(entityManager)));
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return EntityGraphSimpleJpaRepository.class;
    }
}
