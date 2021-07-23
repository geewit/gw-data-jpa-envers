package io.geewit.data.jpa.envers.support;

import io.geewit.data.jpa.envers.repository.EnversRevisionRepository;
import io.geewit.data.jpa.envers.repository.impl.EnversRevisionRepositoryImpl;
import io.geewit.data.jpa.essential.repository.support.EntityGraphJpaRepositoryFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;

import javax.persistence.EntityManager;

/**
 * Repository factory creating {@link EnversRevisionRepository} instances.
 *
 * @author geewit
 */
public class RevisionRepositoryFactory extends EntityGraphJpaRepositoryFactory {

    private final EnversRevisionEntityInformation revisionEntityInformation;

    /**
     * Creates a new {@link RevisionRepositoryFactory} using the given {@link EntityManager} and revision entity class.
     *
     * @param entityManager must not be {@literal null}.
     */
    public RevisionRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.revisionEntityInformation = new EnversRevisionEntityInformation();
    }

    /**
     * Callback to create a {@link JpaRepository} instance with the given {@link EntityManager}
     *
     * @param information will never be {@literal null}.
     * @param entityManager will never be {@literal null}.
     * @return
     */
    @Override
    protected EnversRevisionRepositoryImpl getTargetRepository(RepositoryInformation information,
                                                                         EntityManager entityManager) {

        JpaEntityInformation entityInformation = super.getEntityInformation(information.getDomainType());

        return new EnversRevisionRepositoryImpl<>(entityInformation, revisionEntityInformation, entityManager);
    }

    /**
     * @see JpaRepositoryFactory#getRepositoryBaseClass(org.springframework.data.repository.core.RepositoryMetadata)
     */
    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return EnversRevisionRepositoryImpl.class;
    }

    /**
     * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getRepository(Class, Object)
     */
    @SuppressWarnings("hiding")
    @Override
    public <I> I getRepository(Class<I> repositoryInterface, RepositoryComposition.RepositoryFragments fragments) {

        if (EnversRevisionRepository.class.isAssignableFrom(repositoryInterface)) {
            if (!revisionEntityInformation.getRevisionNumberType().equals(Integer.class)) {
                throw new IllegalStateException(String.format(
                        "Configured a revision entity type of %s with a revision type of %s "
                                + "but the repository interface is typed to a revision type of %s!",
                        repositoryInterface, revisionEntityInformation.getRevisionNumberType(), Integer.class));
            }
        }

        return super.getRepository(repositoryInterface, fragments);
    }
}
