package io.geewit.data.jpa.envers.repository;

import io.geewit.data.jpa.envers.EnversRevisionEntity;
import org.springframework.data.repository.history.support.RevisionEntityInformation;

/**
 * {@link RevisionEntityInformation} for {@link org.hibernate.envers.DefaultRevisionEntity}.
 *
 * @author Oliver Gierke
 * @author geewit
 */
class EnversRevisionEntityInformation implements RevisionEntityInformation {

	/**
	 * @see org.springframework.data.repository.history.support.RevisionEntityInformation#getRevisionNumberType()
	 */
	@Override
	public Class<Integer> getRevisionNumberType() {
		return Integer.class;
	}

	/**
	 * @see org.springframework.data.repository.history.support.RevisionEntityInformation#isDefaultRevisionEntity()
	 */
	@Override
	public boolean isDefaultRevisionEntity() {
		return true;
	}

	/**
	 * @see org.springframework.data.repository.history.support.RevisionEntityInformation#getRevisionEntityClass()
	 */
	@Override
	public Class<EnversRevisionEntity> getRevisionEntityClass() {
		return EnversRevisionEntity.class;
	}
}

