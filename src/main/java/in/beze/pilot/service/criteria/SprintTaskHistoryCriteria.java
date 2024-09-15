package in.beze.pilot.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link in.beze.pilot.domain.SprintTaskHistory} entity. This class is used
 * in {@link in.beze.pilot.web.rest.SprintTaskHistoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sprint-task-histories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SprintTaskHistoryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter comments;

    private LocalDateFilter fromDate;

    private LocalDateFilter toDate;

    private LongFilter fromStatusId;

    private LongFilter toStatusId;

    private Boolean distinct;

    public SprintTaskHistoryCriteria() {}

    public SprintTaskHistoryCriteria(SprintTaskHistoryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.comments = other.optionalComments().map(StringFilter::copy).orElse(null);
        this.fromDate = other.optionalFromDate().map(LocalDateFilter::copy).orElse(null);
        this.toDate = other.optionalToDate().map(LocalDateFilter::copy).orElse(null);
        this.fromStatusId = other.optionalFromStatusId().map(LongFilter::copy).orElse(null);
        this.toStatusId = other.optionalToStatusId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SprintTaskHistoryCriteria copy() {
        return new SprintTaskHistoryCriteria(this);
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

    public LocalDateFilter getFromDate() {
        return fromDate;
    }

    public Optional<LocalDateFilter> optionalFromDate() {
        return Optional.ofNullable(fromDate);
    }

    public LocalDateFilter fromDate() {
        if (fromDate == null) {
            setFromDate(new LocalDateFilter());
        }
        return fromDate;
    }

    public void setFromDate(LocalDateFilter fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateFilter getToDate() {
        return toDate;
    }

    public Optional<LocalDateFilter> optionalToDate() {
        return Optional.ofNullable(toDate);
    }

    public LocalDateFilter toDate() {
        if (toDate == null) {
            setToDate(new LocalDateFilter());
        }
        return toDate;
    }

    public void setToDate(LocalDateFilter toDate) {
        this.toDate = toDate;
    }

    public LongFilter getFromStatusId() {
        return fromStatusId;
    }

    public Optional<LongFilter> optionalFromStatusId() {
        return Optional.ofNullable(fromStatusId);
    }

    public LongFilter fromStatusId() {
        if (fromStatusId == null) {
            setFromStatusId(new LongFilter());
        }
        return fromStatusId;
    }

    public void setFromStatusId(LongFilter fromStatusId) {
        this.fromStatusId = fromStatusId;
    }

    public LongFilter getToStatusId() {
        return toStatusId;
    }

    public Optional<LongFilter> optionalToStatusId() {
        return Optional.ofNullable(toStatusId);
    }

    public LongFilter toStatusId() {
        if (toStatusId == null) {
            setToStatusId(new LongFilter());
        }
        return toStatusId;
    }

    public void setToStatusId(LongFilter toStatusId) {
        this.toStatusId = toStatusId;
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
        final SprintTaskHistoryCriteria that = (SprintTaskHistoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(comments, that.comments) &&
            Objects.equals(fromDate, that.fromDate) &&
            Objects.equals(toDate, that.toDate) &&
            Objects.equals(fromStatusId, that.fromStatusId) &&
            Objects.equals(toStatusId, that.toStatusId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, comments, fromDate, toDate, fromStatusId, toStatusId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SprintTaskHistoryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalComments().map(f -> "comments=" + f + ", ").orElse("") +
            optionalFromDate().map(f -> "fromDate=" + f + ", ").orElse("") +
            optionalToDate().map(f -> "toDate=" + f + ", ").orElse("") +
            optionalFromStatusId().map(f -> "fromStatusId=" + f + ", ").orElse("") +
            optionalToStatusId().map(f -> "toStatusId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
