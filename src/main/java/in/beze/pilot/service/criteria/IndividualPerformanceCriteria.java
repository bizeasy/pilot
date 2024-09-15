package in.beze.pilot.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link in.beze.pilot.domain.IndividualPerformance} entity. This class is used
 * in {@link in.beze.pilot.web.rest.IndividualPerformanceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /individual-performances?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IndividualPerformanceCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter completedTasks;

    private IntegerFilter velocity;

    private IntegerFilter storyPointsCompleted;

    private LongFilter partyId;

    private LongFilter sprintId;

    private Boolean distinct;

    public IndividualPerformanceCriteria() {}

    public IndividualPerformanceCriteria(IndividualPerformanceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.completedTasks = other.optionalCompletedTasks().map(IntegerFilter::copy).orElse(null);
        this.velocity = other.optionalVelocity().map(IntegerFilter::copy).orElse(null);
        this.storyPointsCompleted = other.optionalStoryPointsCompleted().map(IntegerFilter::copy).orElse(null);
        this.partyId = other.optionalPartyId().map(LongFilter::copy).orElse(null);
        this.sprintId = other.optionalSprintId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public IndividualPerformanceCriteria copy() {
        return new IndividualPerformanceCriteria(this);
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

    public IntegerFilter getCompletedTasks() {
        return completedTasks;
    }

    public Optional<IntegerFilter> optionalCompletedTasks() {
        return Optional.ofNullable(completedTasks);
    }

    public IntegerFilter completedTasks() {
        if (completedTasks == null) {
            setCompletedTasks(new IntegerFilter());
        }
        return completedTasks;
    }

    public void setCompletedTasks(IntegerFilter completedTasks) {
        this.completedTasks = completedTasks;
    }

    public IntegerFilter getVelocity() {
        return velocity;
    }

    public Optional<IntegerFilter> optionalVelocity() {
        return Optional.ofNullable(velocity);
    }

    public IntegerFilter velocity() {
        if (velocity == null) {
            setVelocity(new IntegerFilter());
        }
        return velocity;
    }

    public void setVelocity(IntegerFilter velocity) {
        this.velocity = velocity;
    }

    public IntegerFilter getStoryPointsCompleted() {
        return storyPointsCompleted;
    }

    public Optional<IntegerFilter> optionalStoryPointsCompleted() {
        return Optional.ofNullable(storyPointsCompleted);
    }

    public IntegerFilter storyPointsCompleted() {
        if (storyPointsCompleted == null) {
            setStoryPointsCompleted(new IntegerFilter());
        }
        return storyPointsCompleted;
    }

    public void setStoryPointsCompleted(IntegerFilter storyPointsCompleted) {
        this.storyPointsCompleted = storyPointsCompleted;
    }

    public LongFilter getPartyId() {
        return partyId;
    }

    public Optional<LongFilter> optionalPartyId() {
        return Optional.ofNullable(partyId);
    }

    public LongFilter partyId() {
        if (partyId == null) {
            setPartyId(new LongFilter());
        }
        return partyId;
    }

    public void setPartyId(LongFilter partyId) {
        this.partyId = partyId;
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
        final IndividualPerformanceCriteria that = (IndividualPerformanceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(completedTasks, that.completedTasks) &&
            Objects.equals(velocity, that.velocity) &&
            Objects.equals(storyPointsCompleted, that.storyPointsCompleted) &&
            Objects.equals(partyId, that.partyId) &&
            Objects.equals(sprintId, that.sprintId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, completedTasks, velocity, storyPointsCompleted, partyId, sprintId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IndividualPerformanceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCompletedTasks().map(f -> "completedTasks=" + f + ", ").orElse("") +
            optionalVelocity().map(f -> "velocity=" + f + ", ").orElse("") +
            optionalStoryPointsCompleted().map(f -> "storyPointsCompleted=" + f + ", ").orElse("") +
            optionalPartyId().map(f -> "partyId=" + f + ", ").orElse("") +
            optionalSprintId().map(f -> "sprintId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
