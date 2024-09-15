package in.beze.pilot.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link in.beze.pilot.domain.TeamVelocity} entity. This class is used
 * in {@link in.beze.pilot.web.rest.TeamVelocityResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /team-velocities?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamVelocityCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter sprintVelocity;

    private IntegerFilter averageVelocity;

    private LongFilter sprintId;

    private Boolean distinct;

    public TeamVelocityCriteria() {}

    public TeamVelocityCriteria(TeamVelocityCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.sprintVelocity = other.optionalSprintVelocity().map(IntegerFilter::copy).orElse(null);
        this.averageVelocity = other.optionalAverageVelocity().map(IntegerFilter::copy).orElse(null);
        this.sprintId = other.optionalSprintId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TeamVelocityCriteria copy() {
        return new TeamVelocityCriteria(this);
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

    public IntegerFilter getSprintVelocity() {
        return sprintVelocity;
    }

    public Optional<IntegerFilter> optionalSprintVelocity() {
        return Optional.ofNullable(sprintVelocity);
    }

    public IntegerFilter sprintVelocity() {
        if (sprintVelocity == null) {
            setSprintVelocity(new IntegerFilter());
        }
        return sprintVelocity;
    }

    public void setSprintVelocity(IntegerFilter sprintVelocity) {
        this.sprintVelocity = sprintVelocity;
    }

    public IntegerFilter getAverageVelocity() {
        return averageVelocity;
    }

    public Optional<IntegerFilter> optionalAverageVelocity() {
        return Optional.ofNullable(averageVelocity);
    }

    public IntegerFilter averageVelocity() {
        if (averageVelocity == null) {
            setAverageVelocity(new IntegerFilter());
        }
        return averageVelocity;
    }

    public void setAverageVelocity(IntegerFilter averageVelocity) {
        this.averageVelocity = averageVelocity;
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
        final TeamVelocityCriteria that = (TeamVelocityCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sprintVelocity, that.sprintVelocity) &&
            Objects.equals(averageVelocity, that.averageVelocity) &&
            Objects.equals(sprintId, that.sprintId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sprintVelocity, averageVelocity, sprintId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamVelocityCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSprintVelocity().map(f -> "sprintVelocity=" + f + ", ").orElse("") +
            optionalAverageVelocity().map(f -> "averageVelocity=" + f + ", ").orElse("") +
            optionalSprintId().map(f -> "sprintId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
