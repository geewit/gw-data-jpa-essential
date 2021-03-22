package io.geewit.data.jpa.essential.repository;

import io.geewit.data.jpa.essential.repository.impl.SimpleJpaBatchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;

/**
 * Repository factory creating {@link JpaBatchRepository} instances.
 *
 * @author geewit
 */
public class JpaBatchRepositoryFactory<T, ID> extends JpaRepositoryFactory {


    /**
     * Creates a new {@link JpaBatchRepositoryFactory} using the given {@link EntityManager} and revision entity class.
     *
     * @param entityManager must not be {@literal null}.
     */
    public JpaBatchRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Callback to create a {@link JpaRepository} instance with the given {@link EntityManager}
     *
     * @param information will never be {@literal null}.
     * @param entityManager will never be {@literal null}.
     * @return
     */
    @Override
    protected SimpleJpaBatchRepository<T, ID> getTargetRepository(RepositoryInformation information,
                                                                     EntityManager entityManager) {
        JpaEntityInformation<T, ID> entityInformation = (JpaEntityInformation<T, ID>) getEntityInformation(information.getDomainType());
        return new SimpleJpaBatchRepository<>(entityInformation, entityManager);
    }

    /**
     * @see JpaRepositoryFactory#getRepositoryBaseClass(RepositoryMetadata)
     */
    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleJpaBatchRepository.class;
    }
}
