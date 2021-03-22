package io.geewit.data.jpa.essential.repository;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;


/**
 * {@link FactoryBean} creating {@link JpaBatchRepository} instances.
 *
 * @author Oliver Gierke
 * @author Michael Igler
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class JpaBatchRepositoryFactoryBean<T extends JpaBatchRepository<S, ID>, S, ID> extends JpaRepositoryFactoryBean<T, S, ID> {

	/**
	 * Creates a new {@link JpaBatchRepositoryFactoryBean} for the given repository interface.
	 *
	 * @param repositoryInterface must not be {@literal null}.
	 */
	public JpaBatchRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}


	/**
	 * @see JpaRepositoryFactoryBean#createRepositoryFactory(EntityManager)
	 */
	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		return new JpaBatchRepositoryFactory<>(entityManager);
	}

}
