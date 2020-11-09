package io.geewit.data.jpa.envers;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 数据库变动日志
 @author geewit
 @since  2015-5-18
 */
public abstract class EnversRevisionEntity<O extends Serializable> implements Serializable {
    @RevisionNumber
    protected O id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08")
    @RevisionTimestamp
    protected Date revisionTime;

    /**
     * @return 版本主键
     */
    @SuppressWarnings({"unused"})
    public O getId() {
        return this.id;
    }

    /**
     * 版本主键
     * @param id 版本主键
     */
    public abstract void setId(O id);

    @SuppressWarnings({"unused"})
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "revision_time")
    public Date getRevisionTime() {
        return revisionTime;
    }

    @SuppressWarnings({"unused"})
    public void setRevisionTime(Date revisionTime) {
        this.revisionTime = revisionTime;
    }

    @Transient
    public abstract O getOperatorId();

    @SuppressWarnings({"unused"})
    public abstract void setOperatorId(O operatorId);

    @Transient
    public abstract String getOperatorName();

    @SuppressWarnings({"unused"})
    public abstract void setOperatorName(String operatorName);
}
