package in.beze.pilot.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link in.beze.pilot.domain.ProjectAttachment} entity. This class is used
 * in {@link in.beze.pilot.web.rest.ProjectAttachmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /project-attachments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectAttachmentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter facilityId;

    private LongFilter attachmentId;

    private Boolean distinct;

    public ProjectAttachmentCriteria() {}

    public ProjectAttachmentCriteria(ProjectAttachmentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.facilityId = other.optionalFacilityId().map(LongFilter::copy).orElse(null);
        this.attachmentId = other.optionalAttachmentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProjectAttachmentCriteria copy() {
        return new ProjectAttachmentCriteria(this);
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

    public LongFilter getFacilityId() {
        return facilityId;
    }

    public Optional<LongFilter> optionalFacilityId() {
        return Optional.ofNullable(facilityId);
    }

    public LongFilter facilityId() {
        if (facilityId == null) {
            setFacilityId(new LongFilter());
        }
        return facilityId;
    }

    public void setFacilityId(LongFilter facilityId) {
        this.facilityId = facilityId;
    }

    public LongFilter getAttachmentId() {
        return attachmentId;
    }

    public Optional<LongFilter> optionalAttachmentId() {
        return Optional.ofNullable(attachmentId);
    }

    public LongFilter attachmentId() {
        if (attachmentId == null) {
            setAttachmentId(new LongFilter());
        }
        return attachmentId;
    }

    public void setAttachmentId(LongFilter attachmentId) {
        this.attachmentId = attachmentId;
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
        final ProjectAttachmentCriteria that = (ProjectAttachmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(facilityId, that.facilityId) &&
            Objects.equals(attachmentId, that.attachmentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, facilityId, attachmentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectAttachmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFacilityId().map(f -> "facilityId=" + f + ", ").orElse("") +
            optionalAttachmentId().map(f -> "attachmentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
