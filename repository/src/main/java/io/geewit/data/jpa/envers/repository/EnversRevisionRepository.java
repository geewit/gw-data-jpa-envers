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
    /**
     * 查找特定主键id的分页审计列表
     * @param id 主键id
     * @param pageable 分页
     * @return 分页审计列表
     */
    Page<ComparedRevision<T, O>> findComparedRevisions(ID id, Pageable pageable);

    /**
     * 查找特定主键id指定时间前最后一个版本
     * @param id 主键id
     * @param updateTime 指定时间
     * @return 最后一个版本
     */
    T findRevisionByLastUpdateTime(ID id, Date updateTime);
}
