package io.geewit.data.jpa.envers.repository.impl;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import io.geewit.data.jpa.envers.EnversRevisionEntity;
import io.geewit.data.jpa.envers.domain.ComparedRevision;
import io.geewit.data.jpa.envers.repository.EnversRevisionMetadata;
import io.geewit.data.jpa.envers.repository.EnversRevisionRepository;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.RevisionSort;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.history.support.RevisionEntityInformation;
import org.springframework.util.Assert;


/**
 * Repository implementation using Hibernate Envers to implement revision specific query methods.
 *
 * @author Oliver Gierke
 * @author Philipp Huegelmeyer
 * @author Michael Igler
 */
public class EnversRevisionRepositoryImpl<T, ID, O extends Serializable>
        extends SimpleJpaRepository<T, ID> implements EnversRevisionRepository<T, ID, O> {

    private final EntityInformation<T, ID> entityInformation;
    private final RevisionEntityInformation revisionEntityInformation;
    private final EntityManager entityManager;

    /**
     * Creates a new {@link EnversRevisionRepositoryImpl} using the given {@link JpaEntityInformation},
     * {@link RevisionEntityInformation} and {@link EntityManager}.
     *
     * @param entityInformation         must not be {@literal null}.
     * @param revisionEntityInformation must not be {@literal null}.
     * @param entityManager             must not be {@literal null}.
     */
    public EnversRevisionRepositoryImpl(JpaEntityInformation<T, ID> entityInformation,
                                        RevisionEntityInformation revisionEntityInformation, EntityManager entityManager) {

        super(entityInformation, entityManager);

        Assert.notNull(revisionEntityInformation, "[Assertion failed] - revisionEntityInformation must be null");

        this.entityInformation = entityInformation;
        this.revisionEntityInformation = revisionEntityInformation;
        this.entityManager = entityManager;
    }

    /**
     * @see org.springframework.data.repository.history.RevisionRepository#findLastChangeRevision(Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Optional<Revision<Integer, T>> findLastChangeRevision(ID id) {

        Class<T> type = entityInformation.getJavaType();
        AuditReader reader = AuditReaderFactory.get(entityManager);

        List<Number> revisions = reader.getRevisions(type, id);

        if (revisions.isEmpty()) {
            return Optional.empty();
        }

        Number latestRevision = revisions.get(revisions.size() - 1);

        EnversRevisionEntity<O> revisionEntity = reader.findRevision(EnversRevisionEntity.class, latestRevision);
        RevisionMetadata<Integer> metadata = getRevisionMetadata(revisionEntity);
        return Optional.of(Revision.of(metadata, reader.find(type, id, latestRevision)));
    }

    /**
     * @see org.springframework.data.repository.history.RevisionRepository#findRevisions
     */
    @Override
    public Optional<Revision<Integer, T>> findRevision(ID id, Integer revisionNumber) {

        Assert.notNull(id, "Identifier must not be null!");
        Assert.notNull(revisionNumber, "Revision number must not be null!");

        return getEntityForRevision(revisionNumber, id, AuditReaderFactory.get(entityManager));
    }

    /**
     * @see org.springframework.data.repository.history.RevisionRepository#findRevisions(Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Revisions<Integer, T> findRevisions(ID id) {

        Class<T> type = entityInformation.getJavaType();
        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<? extends Number> revisionNumbers = reader.getRevisions(type, id);

        return revisionNumbers.isEmpty() ? Revisions.none()
                : getEntitiesForRevisions((List<Integer>) revisionNumbers, id, reader);
    }

    /**
     * (non-Javadoc)
     * @see org.springframework.data.repository.history.RevisionRepository#findRevisions(Object, org.springframework.data.domain.Pageable)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Page<Revision<Integer, T>> findRevisions(ID id, Pageable pageable) {

        Class<T> type = entityInformation.getJavaType();
        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<Number> revisionNumbers = reader.getRevisions(type, id);
        boolean isDescending = RevisionSort.getRevisionDirection(pageable.getSort()).isDescending();

        if (isDescending) {
            Collections.reverse(revisionNumbers);
        }
        long offset = pageable.getOffset();
        if (offset > revisionNumbers.size()) {
            return new PageImpl<>(Collections.<Revision<Integer, T>>emptyList(), pageable, 0);
        }

        long upperBound = Math.min(revisionNumbers.size(), (offset + pageable.getPageSize()));

        List<? extends Number> subList = revisionNumbers.subList(Math.toIntExact(offset), Math.toIntExact(upperBound));
        Revisions<Integer, T> revisions = getEntitiesForRevisions((List<Integer>) subList, id, reader);

        revisions = isDescending ? revisions.reverse() : revisions;

        return new PageImpl<>(revisions.getContent(), pageable, revisionNumbers.size());
    }


    /**
     * (non-Javadoc)
     * @see org.springframework.data.repository.history.RevisionRepository#findRevisions(Object, Pageable)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Page<ComparedRevision<T, O>> findComparedRevisions(ID id, Pageable pageable) {
        Class<T> type = this.entityInformation.getJavaType();
        AuditReader reader = AuditReaderFactory.get(this.entityManager);
        List<Number> revisionNumbers = reader.getRevisions(type, id);
        boolean isDescending = RevisionSort.getRevisionDirection(pageable.getSort()).isDescending();

        if (isDescending) {
            Collections.reverse(revisionNumbers);
        }
        long offset = pageable.getOffset();
        int revisionNumberSize = revisionNumbers.size();
        if (revisionNumberSize == 0 || offset > revisionNumbers.size()) {
            return new PageImpl(Collections.emptyList(), pageable, 0L);
        }
        long upperBound = Math.min(revisionNumberSize, offset + pageable.getPageSize());
        Number previousOfFirst = null;
        if (offset > 0) {
            previousOfFirst = revisionNumbers.get(Math.toIntExact(offset - 1));
        }
        List<Number> subList = revisionNumbers.subList(Math.toIntExact(offset), Math.toIntExact(upperBound));

        List<ComparedRevision<T, O>> comparedRevisions = getComparedEntitiesForRevisions(id, subList, previousOfFirst, reader);

        return new PageImpl<>(comparedRevisions, pageable, revisionNumberSize);
    }

    @Override
    public T findRevisionByLastUpdateTime(ID id, Date updateTime) {
        Class<T> type = this.entityInformation.getJavaType();
        AuditReader reader = AuditReaderFactory.get(this.entityManager);
        T revision = reader.find(type, id, updateTime);
        return revision;
    }

    /**
     * Returns the entities in the given revisions for the entitiy with the given id.
     *
     * @param revisionNumbers
     * @param id
     * @param reader
     * @return
     */
    @SuppressWarnings("unchecked")
    private Revisions<Integer, T> getEntitiesForRevisions(Collection<Integer> revisionNumbers, ID id, AuditReader reader) {

        Class<T> type = entityInformation.getJavaType();
        Map<Integer, T> revisions = new HashMap<>(revisionNumbers.size());
        Class<T> revisionEntityClass = (Class<T>) revisionEntityInformation.getRevisionEntityClass();
        Map<Number, T> revisionEntities = reader.findRevisions(revisionEntityClass, new HashSet<>(revisionNumbers));

        for (Integer number : revisionNumbers) {
            revisions.put(number, reader.find(type, id, number));
        }

        return Revisions.of(toRevisions(revisions, revisionEntities));
    }

    /**
     * Returns the entities in the given revisions for the entitiy with the given id.
     *
     * @param revisionNumbers
     * @param reader
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<ComparedRevision<T, O>> getComparedEntitiesForRevisions(ID id, List<Number> revisionNumbers, Number previousOfFirst, AuditReader reader) {
        Class<T> type = this.entityInformation.getJavaType();
        List<ComparedRevision<T, O>> list = new ArrayList<>(revisionNumbers.size());

        Set<Number> revisionNumberSet = new HashSet<>(revisionNumbers);
        if (previousOfFirst != null) {
            revisionNumberSet.add(previousOfFirst);
        }
        Map<Number, EnversRevisionEntity> revisionEntities = reader.findRevisions(EnversRevisionEntity.class, revisionNumberSet);
        T current = null;
        T previous = null;
        int revisionNumbersSize = revisionNumbers.size();
        for (int i = 0; i < revisionNumbersSize; i++) {
            if (i == 0) {
                if (previousOfFirst != null) {
                    previous = reader.find(type, id, previousOfFirst);
                }
            } else {
                previous = current;
            }
            current = reader.find(type, id, revisionNumbers.get(i));
            EnversRevisionMetadata<O> metadata = (EnversRevisionMetadata<O>) getRevisionMetadata(revisionEntities.get(revisionNumbers.get(i)));
            Instant updateJodaTime;
            if(metadata.getRevisionInstant().isPresent()) {
                updateJodaTime = metadata.getRevisionInstant().get();
            } else {
                updateJodaTime = null;
            }

            O operatorId = metadata.getOperatorId();
            String operatorName = metadata.getOperatorName();
            ComparedRevision<T, O> comparedRevision = new ComparedRevision(current, previous, updateJodaTime, operatorId, operatorName);
            list.add(comparedRevision);
        }
        return list;
    }


    /**
     * Returns an entity in the given revision for the given entity-id.
     *
     * @param revisionNumber
     * @param id
     * @param reader
     * @return
     */
    @SuppressWarnings("unchecked")
    private Optional<Revision<Integer, T>> getEntityForRevision(Integer revisionNumber, ID id, AuditReader reader) {


        EnversRevisionEntity revision = reader.findRevision(EnversRevisionEntity.class, revisionNumber);
        T entity = reader.find(entityInformation.getJavaType(), id, revisionNumber);
        return Optional.of(Revision.of(getRevisionMetadata(revision), entity));
    }

    @SuppressWarnings("unchecked")
    private List<Revision<Integer, T>> toRevisions(Map<Integer, T> source, Map<Number, T> revisionEntities) {

        List<Revision<Integer, T>> result = new ArrayList<>();

        for (Entry<Integer, T> revision : source.entrySet()) {

            Integer revisionNumber = revision.getKey();
            T entity = revision.getValue();
            RevisionMetadata<Integer> metadata = getRevisionMetadata((EnversRevisionEntity) revisionEntities.get(revisionNumber));
            result.add(Revision.of(metadata, entity));
        }

        Collections.sort(result);
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns the {@link RevisionMetadata} wrapper depending on the type of the given object.
     *
     * @param revisionEntity 版本实体
     * @return
     */
    private EnversRevisionMetadata<O> getRevisionMetadata(EnversRevisionEntity<O> revisionEntity) {
        return new EnversRevisionMetadata<>(revisionEntity);
    }
}
