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
    @Transient
    @SuppressWarnings({"unused"})
    public O getId() {
        return this.id;
    }

    /**
     * 版本主键
     * @param id 版本主键
     */
    @SuppressWarnings({"unused"})
    public abstract void setId(O id);

    @Transient
    @SuppressWarnings({"unused"})
    public Date getRevisionTime() {
        return revisionTime;
    }

    /**
     * 版本时间
     * @param revisionTime 版本时间
     */
    @SuppressWarnings({"unused"})
    public abstract void setRevisionTime(Date revisionTime);

    /**
     * @return 操作人id
     */
    @Transient
    public abstract O getOperatorId();

    /**
     * @param operatorId 操作人id
     */
    @SuppressWarnings({"unused"})
    public abstract void setOperatorId(O operatorId);

    /**
     * 操作人名
     */
    @Transient
    public abstract String getOperatorName();

    /**
     * 操作人名
     * @param operatorName 操作人名
     */
    @SuppressWarnings({"unused"})
    public abstract void setOperatorName(String operatorName);
}
