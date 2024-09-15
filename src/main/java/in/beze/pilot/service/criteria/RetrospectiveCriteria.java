package in.beze.pilot.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link in.beze.pilot.domain.Retrospective} entity. This class is used
 * in {@link in.beze.pilot.web.rest.RetrospectiveResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /retrospectives?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RetrospectiveCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter summary;

    private StringFilter actionItems;

    private InstantFilter dateCreated;

    private LongFilter sprintId;

    private Boolean distinct;

    public RetrospectiveCriteria() {}

    public RetrospectiveCriteria(RetrospectiveCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.summary = other.optionalSummary().map(StringFilter::copy).orElse(null);
        this.actionItems = other.optionalActionItems().map(StringFilter::copy).orElse(null);
        this.dateCreated = other.optionalDateCreated().map(InstantFilter::copy).orElse(null);
        this.sprintId = other.optionalSprintId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RetrospectiveCriteria copy() {
        return new RetrospectiveCriteria(this);
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

    public StringFilter getSummary() {
        return summary;
    }

    public Optional<StringFilter> optionalSummary() {
        return Optional.ofNullable(summary);
    }

    public StringFilter summary() {
        if (summary == null) {
            setSummary(new StringFilter());
        }
        return summary;
    }

    public void setSummary(StringFilter summary) {
        this.summary = summary;
    }

    public StringFilter getActionItems() {
        return actionItems;
    }

    public Optional<StringFilter> optionalActionItems() {
        return Optional.ofNullable(actionItems);
    }

    public StringFilter actionItems() {
        if (actionItems == null) {
            setActionItems(new StringFilter());
        }
        return actionItems;
    }

    public void setActionItems(StringFilter actionItems) {
        this.actionItems = actionItems;
    }

    public InstantFilter getDateCreated() {
        return dateCreated;
    }

    public Optional<InstantFilter> optionalDateCreated() {
        return Optional.ofNullable(dateCreated);
    }

    public InstantFilter dateCreated() {
        if (dateCreated == null) {
            setDateCreated(new InstantFilter());
        }
        return dateCreated;
    }

    public void setDateCreated(InstantFilter dateCreated) {
        this.dateCreated = dateCreated;
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
        final RetrospectiveCriteria that = (RetrospectiveCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(summary, that.summary) &&
            Objects.equals(actionItems, that.actionItems) &&
            Objects.equals(dateCreated, that.dateCreated) &&
            Objects.equals(sprintId, that.sprintId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, summary, actionItems, dateCreated, sprintId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RetrospectiveCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSummary().map(f -> "summary=" + f + ", ").orElse("") +
            optionalActionItems().map(f -> "actionItems=" + f + ", ").orElse("") +
            optionalDateCreated().map(f -> "dateCreated=" + f + ", ").orElse("") +
            optionalSprintId().map(f -> "sprintId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
