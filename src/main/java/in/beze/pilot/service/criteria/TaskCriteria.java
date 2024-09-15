package in.beze.pilot.service.criteria;

import in.beze.pilot.domain.enumeration.TaskPriority;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link in.beze.pilot.domain.Task} entity. This class is used
 * in {@link in.beze.pilot.web.rest.TaskResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tasks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaskCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TaskPriority
     */
    public static class TaskPriorityFilter extends Filter<TaskPriority> {

        public TaskPriorityFilter() {}

        public TaskPriorityFilter(TaskPriorityFilter filter) {
            super(filter);
        }

        @Override
        public TaskPriorityFilter copy() {
            return new TaskPriorityFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter description;

    private TaskPriorityFilter priority;

    private LocalDateFilter dueDate;

    private IntegerFilter storyPoints;

    private InstantFilter startTime;

    private InstantFilter pauseTime;

    private InstantFilter endTime;

    private DurationFilter totalDuration;

    private IntegerFilter sequenceNo;

    private LongFilter commentsId;

    private LongFilter projectId;

    private LongFilter statusId;

    private LongFilter assigneeId;

    private Boolean distinct;

    public TaskCriteria() {}

    public TaskCriteria(TaskCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.priority = other.optionalPriority().map(TaskPriorityFilter::copy).orElse(null);
        this.dueDate = other.optionalDueDate().map(LocalDateFilter::copy).orElse(null);
        this.storyPoints = other.optionalStoryPoints().map(IntegerFilter::copy).orElse(null);
        this.startTime = other.optionalStartTime().map(InstantFilter::copy).orElse(null);
        this.pauseTime = other.optionalPauseTime().map(InstantFilter::copy).orElse(null);
        this.endTime = other.optionalEndTime().map(InstantFilter::copy).orElse(null);
        this.totalDuration = other.optionalTotalDuration().map(DurationFilter::copy).orElse(null);
        this.sequenceNo = other.optionalSequenceNo().map(IntegerFilter::copy).orElse(null);
        this.commentsId = other.optionalCommentsId().map(LongFilter::copy).orElse(null);
        this.projectId = other.optionalProjectId().map(LongFilter::copy).orElse(null);
        this.statusId = other.optionalStatusId().map(LongFilter::copy).orElse(null);
        this.assigneeId = other.optionalAssigneeId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TaskCriteria copy() {
        return new TaskCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public TaskPriorityFilter getPriority() {
        return priority;
    }

    public Optional<TaskPriorityFilter> optionalPriority() {
        return Optional.ofNullable(priority);
    }

    public TaskPriorityFilter priority() {
        if (priority == null) {
            setPriority(new TaskPriorityFilter());
        }
        return priority;
    }

    public void setPriority(TaskPriorityFilter priority) {
        this.priority = priority;
    }

    public LocalDateFilter getDueDate() {
        return dueDate;
    }

    public Optional<LocalDateFilter> optionalDueDate() {
        return Optional.ofNullable(dueDate);
    }

    public LocalDateFilter dueDate() {
        if (dueDate == null) {
            setDueDate(new LocalDateFilter());
        }
        return dueDate;
    }

    public void setDueDate(LocalDateFilter dueDate) {
        this.dueDate = dueDate;
    }

    public IntegerFilter getStoryPoints() {
        return storyPoints;
    }

    public Optional<IntegerFilter> optionalStoryPoints() {
        return Optional.ofNullable(storyPoints);
    }

    public IntegerFilter storyPoints() {
        if (storyPoints == null) {
            setStoryPoints(new IntegerFilter());
        }
        return storyPoints;
    }

    public void setStoryPoints(IntegerFilter storyPoints) {
        this.storyPoints = storyPoints;
    }

    public InstantFilter getStartTime() {
        return startTime;
    }

    public Optional<InstantFilter> optionalStartTime() {
        return Optional.ofNullable(startTime);
    }

    public InstantFilter startTime() {
        if (startTime == null) {
            setStartTime(new InstantFilter());
        }
        return startTime;
    }

    public void setStartTime(InstantFilter startTime) {
        this.startTime = startTime;
    }

    public InstantFilter getPauseTime() {
        return pauseTime;
    }

    public Optional<InstantFilter> optionalPauseTime() {
        return Optional.ofNullable(pauseTime);
    }

    public InstantFilter pauseTime() {
        if (pauseTime == null) {
            setPauseTime(new InstantFilter());
        }
        return pauseTime;
    }

    public void setPauseTime(InstantFilter pauseTime) {
        this.pauseTime = pauseTime;
    }

    public InstantFilter getEndTime() {
        return endTime;
    }

    public Optional<InstantFilter> optionalEndTime() {
        return Optional.ofNullable(endTime);
    }

    public InstantFilter endTime() {
        if (endTime == null) {
            setEndTime(new InstantFilter());
        }
        return endTime;
    }

    public void setEndTime(InstantFilter endTime) {
        this.endTime = endTime;
    }

    public DurationFilter getTotalDuration() {
        return totalDuration;
    }

    public Optional<DurationFilter> optionalTotalDuration() {
        return Optional.ofNullable(totalDuration);
    }

    public DurationFilter totalDuration() {
        if (totalDuration == null) {
            setTotalDuration(new DurationFilter());
        }
        return totalDuration;
    }

    public void setTotalDuration(DurationFilter totalDuration) {
        this.totalDuration = totalDuration;
    }

    public IntegerFilter getSequenceNo() {
        return sequenceNo;
    }

    public Optional<IntegerFilter> optionalSequenceNo() {
        return Optional.ofNullable(sequenceNo);
    }

    public IntegerFilter sequenceNo() {
        if (sequenceNo == null) {
            setSequenceNo(new IntegerFilter());
        }
        return sequenceNo;
    }

    public void setSequenceNo(IntegerFilter sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public LongFilter getCommentsId() {
        return commentsId;
    }

    public Optional<LongFilter> optionalCommentsId() {
        return Optional.ofNullable(commentsId);
    }

    public LongFilter commentsId() {
        if (commentsId == null) {
            setCommentsId(new LongFilter());
        }
        return commentsId;
    }

    public void setCommentsId(LongFilter commentsId) {
        this.commentsId = commentsId;
    }

    public LongFilter getProjectId() {
        return projectId;
    }

    public Optional<LongFilter> optionalProjectId() {
        return Optional.ofNullable(projectId);
    }

    public LongFilter projectId() {
        if (projectId == null) {
            setProjectId(new LongFilter());
        }
        return projectId;
    }

    public void setProjectId(LongFilter projectId) {
        this.projectId = projectId;
    }

    public LongFilter getStatusId() {
        return statusId;
    }

    public Optional<LongFilter> optionalStatusId() {
        return Optional.ofNullable(statusId);
    }

    public LongFilter statusId() {
        if (statusId == null) {
            setStatusId(new LongFilter());
        }
        return statusId;
    }

    public void setStatusId(LongFilter statusId) {
        this.statusId = statusId;
    }

    public LongFilter getAssigneeId() {
        return assigneeId;
    }

    public Optional<LongFilter> optionalAssigneeId() {
        return Optional.ofNullable(assigneeId);
    }

    public LongFilter assigneeId() {
        if (assigneeId == null) {
            setAssigneeId(new LongFilter());
        }
        return assigneeId;
    }

    public void setAssigneeId(LongFilter assigneeId) {
        this.assigneeId = assigneeId;
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
        final TaskCriteria that = (TaskCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(priority, that.priority) &&
            Objects.equals(dueDate, that.dueDate) &&
            Objects.equals(storyPoints, that.storyPoints) &&
            Objects.equals(startTime, that.startTime) &&
            Objects.equals(pauseTime, that.pauseTime) &&
            Objects.equals(endTime, that.endTime) &&
            Objects.equals(totalDuration, that.totalDuration) &&
            Objects.equals(sequenceNo, that.sequenceNo) &&
            Objects.equals(commentsId, that.commentsId) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(statusId, that.statusId) &&
            Objects.equals(assigneeId, that.assigneeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            description,
            priority,
            dueDate,
            storyPoints,
            startTime,
            pauseTime,
            endTime,
            totalDuration,
            sequenceNo,
            commentsId,
            projectId,
            statusId,
            assigneeId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalPriority().map(f -> "priority=" + f + ", ").orElse("") +
            optionalDueDate().map(f -> "dueDate=" + f + ", ").orElse("") +
            optionalStoryPoints().map(f -> "storyPoints=" + f + ", ").orElse("") +
            optionalStartTime().map(f -> "startTime=" + f + ", ").orElse("") +
            optionalPauseTime().map(f -> "pauseTime=" + f + ", ").orElse("") +
            optionalEndTime().map(f -> "endTime=" + f + ", ").orElse("") +
            optionalTotalDuration().map(f -> "totalDuration=" + f + ", ").orElse("") +
            optionalSequenceNo().map(f -> "sequenceNo=" + f + ", ").orElse("") +
            optionalCommentsId().map(f -> "commentsId=" + f + ", ").orElse("") +
            optionalProjectId().map(f -> "projectId=" + f + ", ").orElse("") +
            optionalStatusId().map(f -> "statusId=" + f + ", ").orElse("") +
            optionalAssigneeId().map(f -> "assigneeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
