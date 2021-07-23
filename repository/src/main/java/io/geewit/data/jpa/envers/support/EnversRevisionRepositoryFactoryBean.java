package io.geewit.data.jpa.envers.support;

import javax.persistence.EntityManager;
import io.geewit.data.jpa.envers.repository.EnversRevisionRepository;
import io.geewit.data.jpa.essential.repository.support.EntityGraphJpaRepositoryFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;


/**
 * {@link FactoryBean} creating {@link EnversRevisionRepository} instances.
 *
 * @author Oliver Gierke
 * @author Michael Igler
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class EnversRevisionRepositoryFactoryBean<T extends EnversRevisionRepository<S, ID, O>, S, ID extends Serializable, O extends Serializable> extends EntityGraphJpaRepositoryFactoryBean<T, S, ID> {

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
		return new RevisionRepositoryFactory(entityManager);
	}

}
