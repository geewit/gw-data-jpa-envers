package io.geewit.data.jpa.envers.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.Instant;

/**
 *
 * @author geewit
 * @since  2017-05-26
 */
public class ComparedRevision<T, O> implements Comparable<ComparedRevision<T, O>>, Serializable {
    public ComparedRevision(Number revision, T current, T previous, Instant updateTime, O operatorId, String operatorName) {
        this.revision = revision;
        this.current = current;
        this.previous = previous;
        this.updateTime = updateTime;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
    }

    private Number revision;

    private T current;

    private T previous;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08")
    private Instant updateTime;

    private O operatorId;

    private String operatorName;

    public Number getRevision() {
        return revision;
    }

    public void setRevision(Number revision) {
        this.revision = revision;
    }

    @SuppressWarnings({"unused"})
    public T getCurrent() {
        return current;
    }

    @SuppressWarnings({"unused"})
    public void setCurrent(T current) {
        this.current = current;
    }

    @SuppressWarnings({"unused"})
    public T getPrevious() {
        return previous;
    }

    @SuppressWarnings({"unused"})
    public void setPrevious(T previous) {
        this.previous = previous;
    }

    @SuppressWarnings({"unused"})
    public Instant getUpdateTime() {
        return updateTime;
    }

    @SuppressWarnings({"unused"})
    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    @SuppressWarnings({"unused"})
    public O getOperatorId() {
        return operatorId;
    }

    @SuppressWarnings({"unused"})
    public void setOperatorId(O operatorId) {
        this.operatorId = operatorId;
    }

    @SuppressWarnings({"unused"})
    public String getOperatorName() {
        return operatorName;
    }

    @SuppressWarnings({"unused"})
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    @Override
    public int compareTo(ComparedRevision<T, O> that) {
        if(this.revision != null && that.revision == null) {
            return 1;
        }
        if(this.revision == null && that.revision != null) {
            return -1;
        }
        if(this.revision == null) {
            return 0;
        }
        if(this.revision.longValue() == that.getRevision().longValue()) {
            return 0;
        }
        return this.revision.longValue() > that.getRevision().longValue() ? 1 : -1;
    }
}
