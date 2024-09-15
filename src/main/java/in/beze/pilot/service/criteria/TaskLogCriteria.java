package in.beze.pilot.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link in.beze.pilot.domain.TaskLog} entity. This class is used
 * in {@link in.beze.pilot.web.rest.TaskLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /task-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaskLogCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter comments;

    private InstantFilter fromTime;

    private InstantFilter toTime;

    private LongFilter taskId;

    private Boolean distinct;

    public TaskLogCriteria() {}

    public TaskLogCriteria(TaskLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.comments = other.optionalComments().map(StringFilter::copy).orElse(null);
        this.fromTime = other.optionalFromTime().map(InstantFilter::copy).orElse(null);
        this.toTime = other.optionalToTime().map(InstantFilter::copy).orElse(null);
        this.taskId = other.optionalTaskId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TaskLogCriteria copy() {
        return new TaskLogCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getComments() {
        return comments;
    }

    public Optional<StringFilter> optionalComments() {
        return Optional.ofNullable(comments);
    }

    public StringFilter comments() {
        if (comments == null) {
            setComments(new StringFilter());
        }
        return comments;
    }

    public void setComments(StringFilter comments) {
        this.comments = comments;
    }

    public InstantFilter getFromTime() {
        return fromTime;
    }

    public Optional<InstantFilter> optionalFromTime() {
        return Optional.ofNullable(fromTime);
    }

    public InstantFilter fromTime() {
        if (fromTime == null) {
            setFromTime(new InstantFilter());
        }
        return fromTime;
    }

    public void setFromTime(InstantFilter fromTime) {
        this.fromTime = fromTime;
    }

    public InstantFilter getToTime() {
        return toTime;
    }

    public Optional<InstantFilter> optionalToTime() {
        return Optional.ofNullable(toTime);
    }

    public InstantFilter toTime() {
        if (toTime == null) {
            setToTime(new InstantFilter());
        }
        return toTime;
    }

    public void setToTime(InstantFilter toTime) {
        this.toTime = toTime;
    }

    public LongFilter getTaskId() {
        return taskId;
    }

    public Optional<LongFilter> optionalTaskId() {
        return Optional.ofNullable(taskId);
    }

    public LongFilter taskId() {
        if (taskId == null) {
            setTaskId(new LongFilter());
        }
        return taskId;
    }

    public void setTaskId(LongFilter taskId) {
        this.taskId = taskId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TaskLogCriteria that = (TaskLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(comments, that.comments) &&
            Objects.equals(fromTime, that.fromTime) &&
            Objects.equals(toTime, that.toTime) &&
            Objects.equals(taskId, that.taskId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, comments, fromTime, toTime, taskId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalComments().map(f -> "comments=" + f + ", ").orElse("") +
            optionalFromTime().map(f -> "fromTime=" + f + ", ").orElse("") +
            optionalToTime().map(f -> "toTime=" + f + ", ").orElse("") +
            optionalTaskId().map(f -> "taskId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
