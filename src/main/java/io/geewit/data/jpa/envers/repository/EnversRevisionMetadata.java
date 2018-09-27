package io.geewit.data.jpa.envers.repository;

import io.geewit.data.jpa.envers.EnversRevisionEntity;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.util.AnnotationDetectionFieldCallback;
import org.springframework.data.util.Lazy;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * {@link RevisionMetadata} working with a {@link DefaultRevisionEntity}.
 *
 * @author Oliver Gierke
 * @author Philip Huegelmeyer
 */
public class EnversRevisionMetadata<O extends Serializable> implements RevisionMetadata<Integer> {

	private final EnversRevisionEntity<O> entity;
    private final Lazy<Optional<Integer>> revisionNumber;
    private final Lazy<Optional<LocalDateTime>> revisionDate;

	/**
	 * Creates a new {@link EnversRevisionMetadata}.
	 *
	 * @param entity must not be {@literal null}.
	 */
	public EnversRevisionMetadata(EnversRevisionEntity<O> entity) {

		Assert.notNull(entity, "[Assertion failed] - DefaultRevisionEntity argument must be null");
		this.entity = entity;
        this.revisionNumber = detectAnnotation(entity, RevisionNumber.class);
        this.revisionDate = detectAnnotation(entity, RevisionTimestamp.class);
	}

	/**
	 * (non-Javadoc)
	 * @see org.springframework.data.history.RevisionMetadata#getRevisionNumber()
	 */
	@Override
	public Optional<Integer> getRevisionNumber() {
		return revisionNumber.get();
	}

	/**
	 * (non-Javadoc)
	 * @see org.springframework.data.history.RevisionMetadata#getRevisionDate()
	 */
	@Override
	public Optional<LocalDateTime> getRevisionDate() {
		return revisionDate.get();
	}

	@Override
	public Optional<Instant> getRevisionInstant() {
		return Optional.empty();
	}

	public O getOperatorId() {
		return entity.getOperatorId();
	}

	@SuppressWarnings("unchecked")
	public String getOperatorName() {
		return entity.getOperatorName();
	}

	/**
	 * (non-Javadoc)
	 * @see org.springframework.data.history.RevisionMetadata#getDelegate()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getDelegate() {
		return (T) entity;
	}

	private static <T> Lazy<Optional<T>> detectAnnotation(Object entity, Class<? extends Annotation> annotationType) {

		return Lazy.of(() -> {
			AnnotationDetectionFieldCallback callback = new AnnotationDetectionFieldCallback(annotationType);
			ReflectionUtils.doWithFields(entity.getClass(), callback);
			return Optional.ofNullable(callback.getValue(entity));
		});
	}
}
