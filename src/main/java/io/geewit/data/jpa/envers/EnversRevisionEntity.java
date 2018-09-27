package io.geewit.data.jpa.envers;

import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 数据库变动日志
 @author gelif
 @since  2015-5-18
 */
@MappedSuperclass
public abstract class EnversRevisionEntity<O extends Serializable> implements Serializable {
    @RevisionNumber
    private Integer id;

    @RevisionTimestamp
    private Date revisionTime;

    @SuppressWarnings({"unused"})
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int")
    public Integer getId() {
        return id;
    }

    @SuppressWarnings({"unused"})
    public void setId(Integer id) {
        this.id = id;
    }


    @SuppressWarnings({"unused"})
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "revision_time", columnDefinition = "timestamp not null comment '版本保存时间'")
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
