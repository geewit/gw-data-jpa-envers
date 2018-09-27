package io.geewit.data.jpa.envers.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author geewit
 * @since  2017-05-26
 */
public class ComparedRevision<T, O> implements Serializable {
    public ComparedRevision(T current, T previous, LocalDateTime updateTime, O operatorId, String operatorName) {
        this.current = current;
        this.previous = previous;
        this.updateTime = updateTime;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
    }

    private T current;

    private T previous;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08")
    private LocalDateTime updateTime;

    private O operatorId;

    private String operatorName;

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
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    @SuppressWarnings({"unused"})
    public void setUpdateTime(LocalDateTime updateTime) {
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
}
