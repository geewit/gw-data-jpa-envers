package io.geewit.data.jpa.envers.repository;

import io.geewit.data.jpa.envers.domain.ComparedRevision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.history.RevisionRepository;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author geewit
 * @since 2017-05-26
 */
@SuppressWarnings({"unused"})
@NoRepositoryBean
public interface EnversRevisionRepository<T, ID, O extends Serializable> extends RevisionRepository<T, ID, Integer> {
    Page<ComparedRevision<T, O>> findComparedRevisions(ID id, Pageable pageable);

    T findRevisionByLastUpdateTime(ID id, Date updateTime);
}
