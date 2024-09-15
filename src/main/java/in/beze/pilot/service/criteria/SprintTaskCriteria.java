package in.beze.pilot.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link in.beze.pilot.domain.SprintTask} entity. This class is used
 * in {@link in.beze.pilot.web.rest.SprintTaskResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sprint-tasks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SprintTaskCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter sequenceNo;

    private IntegerFilter storyPoints;

    private InstantFilter fromTime;

    private InstantFilter thruTime;

    private InstantFilter assignedTime;

    private DurationFilter duration;

    private LongFilter taskId;

    private LongFilter sprintId;

    private LongFilter assignedToId;

    private LongFilter assignedById;

    private LongFilter qaId;

    private LongFilter reviewedById;

    private LongFilter statusId;

    private Boolean distinct;

    public SprintTaskCriteria() {}

    public SprintTaskCriteria(SprintTaskCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.sequenceNo = other.optionalSequenceNo().map(IntegerFilter::copy).orElse(null);
        this.storyPoints = other.optionalStoryPoints().map(IntegerFilter::copy).orElse(null);
        this.fromTime = other.optionalFromTime().map(InstantFilter::copy).orElse(null);
        this.thruTime = other.optionalThruTime().map(InstantFilter::copy).orElse(null);
        this.assignedTime = other.optionalAssignedTime().map(InstantFilter::copy).orElse(null);
        this.duration = other.optionalDuration().map(DurationFilter::copy).orElse(null);
        this.taskId = other.optionalTaskId().map(LongFilter::copy).orElse(null);
        this.sprintId = other.optionalSprintId().map(LongFilter::copy).orElse(null);
        this.assignedToId = other.optionalAssignedToId().map(LongFilter::copy).orElse(null);
        this.assignedById = other.optionalAssignedById().map(LongFilter::copy).orElse(null);
        this.qaId = other.optionalQaId().map(LongFilter::copy).orElse(null);
        this.reviewedById = other.optionalReviewedById().map(LongFilter::copy).orElse(null);
        this.statusId = other.optionalStatusId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SprintTaskCriteria copy() {
        return new SprintTaskCriteria(this);
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

    public InstantFilter getThruTime() {
        return thruTime;
    }

    public Optional<InstantFilter> optionalThruTime() {
        return Optional.ofNullable(thruTime);
    }

    public InstantFilter thruTime() {
        if (thruTime == null) {
            setThruTime(new InstantFilter());
        }
        return thruTime;
    }

    public void setThruTime(InstantFilter thruTime) {
        this.thruTime = thruTime;
    }

    public InstantFilter getAssignedTime() {
        return assignedTime;
    }

    public Optional<InstantFilter> optionalAssignedTime() {
        return Optional.ofNullable(assignedTime);
    }

    public InstantFilter assignedTime() {
        if (assignedTime == null) {
            setAssignedTime(new InstantFilter());
        }
        return assignedTime;
    }

    public void setAssignedTime(InstantFilter assignedTime) {
        this.assignedTime = assignedTime;
    }

    public DurationFilter getDuration() {
        return duration;
    }

    public Optional<DurationFilter> optionalDuration() {
        return Optional.ofNullable(duration);
    }

    public DurationFilter duration() {
        if (duration == null) {
            setDuration(new DurationFilter());
        }
        return duration;
    }

    public void setDuration(DurationFilter duration) {
        this.duration = duration;
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

    public LongFilter getSprintId() {
        return sprintId;
    }

    public Optional<LongFilter> optionalSprintId() {
        return Optional.ofNullable(sprintId);
    }

    public LongFilter sprintId() {
        if (sprintId == null) {
            setSprintId(new LongFilter());
        }
        return sprintId;
    }

    public void setSprintId(LongFilter sprintId) {
        this.sprintId = sprintId;
    }

    public LongFilter getAssignedToId() {
        return assignedToId;
    }

    public Optional<LongFilter> optionalAssignedToId() {
        return Optional.ofNullable(assignedToId);
    }

    public LongFilter assignedToId() {
        if (assignedToId == null) {
            setAssignedToId(new LongFilter());
        }
        return assignedToId;
    }

    public void setAssignedToId(LongFilter assignedToId) {
        this.assignedToId = assignedToId;
    }

    public LongFilter getAssignedById() {
        return assignedById;
    }

    public Optional<LongFilter> optionalAssignedById() {
        return Optional.ofNullable(assignedById);
    }

    public LongFilter assignedById() {
        if (assignedById == null) {
            setAssignedById(new LongFilter());
        }
        return assignedById;
    }

    public void setAssignedById(LongFilter assignedById) {
        this.assignedById = assignedById;
    }

    public LongFilter getQaId() {
        return qaId;
    }

    public Optional<LongFilter> optionalQaId() {
        return Optional.ofNullable(qaId);
    }

    public LongFilter qaId() {
        if (qaId == null) {
            setQaId(new LongFilter());
        }
        return qaId;
    }

    public void setQaId(LongFilter qaId) {
        this.qaId = qaId;
    }

    public LongFilter getReviewedById() {
        return reviewedById;
    }

    public Optional<LongFilter> optionalReviewedById() {
        return Optional.ofNullable(reviewedById);
    }

    public LongFilter reviewedById() {
        if (reviewedById == null) {
            setReviewedById(new LongFilter());
        }
        return reviewedById;
    }

    public void setReviewedById(LongFilter reviewedById) {
        this.reviewedById = reviewedById;
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
        final SprintTaskCriteria that = (SprintTaskCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sequenceNo, that.sequenceNo) &&
            Objects.equals(storyPoints, that.storyPoints) &&
            Objects.equals(fromTime, that.fromTime) &&
            Objects.equals(thruTime, that.thruTime) &&
            Objects.equals(assignedTime, that.assignedTime) &&
            Objects.equals(duration, that.duration) &&
            Objects.equals(taskId, that.taskId) &&
            Objects.equals(sprintId, that.sprintId) &&
            Objects.equals(assignedToId, that.assignedToId) &&
            Objects.equals(assignedById, that.assignedById) &&
            Objects.equals(qaId, that.qaId) &&
            Objects.equals(reviewedById, that.reviewedById) &&
            Objects.equals(statusId, that.statusId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            sequenceNo,
            storyPoints,
            fromTime,
            thruTime,
            assignedTime,
            duration,
            taskId,
            sprintId,
            assignedToId,
            assignedById,
            qaId,
            reviewedById,
            statusId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SprintTaskCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSequenceNo().map(f -> "sequenceNo=" + f + ", ").orElse("") +
            optionalStoryPoints().map(f -> "storyPoints=" + f + ", ").orElse("") +
            optionalFromTime().map(f -> "fromTime=" + f + ", ").orElse("") +
            optionalThruTime().map(f -> "thruTime=" + f + ", ").orElse("") +
            optionalAssignedTime().map(f -> "assignedTime=" + f + ", ").orElse("") +
            optionalDuration().map(f -> "duration=" + f + ", ").orElse("") +
            optionalTaskId().map(f -> "taskId=" + f + ", ").orElse("") +
            optionalSprintId().map(f -> "sprintId=" + f + ", ").orElse("") +
            optionalAssignedToId().map(f -> "assignedToId=" + f + ", ").orElse("") +
            optionalAssignedById().map(f -> "assignedById=" + f + ", ").orElse("") +
            optionalQaId().map(f -> "qaId=" + f + ", ").orElse("") +
            optionalReviewedById().map(f -> "reviewedById=" + f + ", ").orElse("") +
            optionalStatusId().map(f -> "statusId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
