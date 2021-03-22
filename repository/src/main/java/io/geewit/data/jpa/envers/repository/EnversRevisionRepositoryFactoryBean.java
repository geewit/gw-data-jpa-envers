package io.geewit.data.jpa.envers.repository;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;


/**
 * {@link FactoryBean} creating {@link EnversRevisionRepository} instances.
 *
 * @author Oliver Gierke
 * @author Michael Igler
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class EnversRevisionRepositoryFactoryBean<T extends EnversRevisionRepository<S, ID, O>, S, ID, O extends Serializable> extends JpaRepositoryFactoryBean<T, S, ID> {

	/**
	 * Creates a new {@link EnversRevisionRepositoryFactoryBean} for the given repository interface.
	 *
	 * @param repositoryInterface must not be {@literal null}.
	 */
	public EnversRevisionRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}


	/**
	 * @see org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean#createRepositoryFactory(javax.persistence.EntityManager)
	 */
	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		return new RevisionRepositoryFactory<>(entityManager);
	}

}
