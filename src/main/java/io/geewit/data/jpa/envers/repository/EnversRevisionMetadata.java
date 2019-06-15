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
import java.time.ZoneOffset;
import java.util.Date;
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
	private final Lazy<Optional<Object>> revisionDate;

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

	@Override
	public Optional<LocalDateTime> getRevisionDate() {
		return revisionDate.get().map(EnversRevisionMetadata::convertToLocalDateTime);
	}

	@Override
	public Optional<Instant> getRevisionInstant() {
		return revisionDate.get().map(EnversRevisionMetadata::convertToInstant);
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

	private static LocalDateTime convertToLocalDateTime(Object timestamp) {

		if (timestamp instanceof LocalDateTime) {
			return (LocalDateTime) timestamp;
		}

		return LocalDateTime.ofInstant(convertToInstant(timestamp), ZoneOffset.systemDefault());
	}

	private static Instant convertToInstant(Object timestamp) {

		if (timestamp instanceof Instant) {
			return (Instant) timestamp;
		}

		if (timestamp instanceof LocalDateTime) {
			return ((LocalDateTime) timestamp).atZone(ZoneOffset.systemDefault()).toInstant();
		}

		if (timestamp instanceof Long) {
			return Instant.ofEpochMilli((Long) timestamp);
		}

		if (Date.class.isInstance(timestamp)) {
			return Date.class.cast(timestamp).toInstant();
		}

		throw new IllegalArgumentException(String.format("Can't convert %s to Instant!", timestamp));
	}
}
