package in.beze.pilot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SprintTask.
 */
@Entity
@Table(name = "sprint_task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SprintTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "sequence_no")
    private Integer sequenceNo;

    @Column(name = "story_points")
    private Integer storyPoints;

    @Column(name = "from_time")
    private Instant fromTime;

    @Column(name = "thru_time")
    private Instant thruTime;

    @Column(name = "assigned_time")
    private Instant assignedTime;

    @Column(name = "duration")
    private Duration duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "comments", "project", "status", "assignee" }, allowSetters = true)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "project", "status" }, allowSetters = true)
    private Sprint sprint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "status", "partyType" }, allowSetters = true)
    private Party assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "status", "partyType" }, allowSetters = true)
    private Party assignedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "status", "partyType" }, allowSetters = true)
    private Party qa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "status", "partyType" }, allowSetters = true)
    private Party reviewedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "category" }, allowSetters = true)
    private Status status;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SprintTask id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSequenceNo() {
        return this.sequenceNo;
    }

    public SprintTask sequenceNo(Integer sequenceNo) {
        this.setSequenceNo(sequenceNo);
        return this;
    }

    public void setSequenceNo(Integer sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public Integer getStoryPoints() {
        return this.storyPoints;
    }

    public SprintTask storyPoints(Integer storyPoints) {
        this.setStoryPoints(storyPoints);
        return this;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Instant getFromTime() {
        return this.fromTime;
    }

    public SprintTask fromTime(Instant fromTime) {
        this.setFromTime(fromTime);
        return this;
    }

    public void setFromTime(Instant fromTime) {
        this.fromTime = fromTime;
    }

    public Instant getThruTime() {
        return this.thruTime;
    }

    public SprintTask thruTime(Instant thruTime) {
        this.setThruTime(thruTime);
        return this;
    }

    public void setThruTime(Instant thruTime) {
        this.thruTime = thruTime;
    }

    public Instant getAssignedTime() {
        return this.assignedTime;
    }

    public SprintTask assignedTime(Instant assignedTime) {
        this.setAssignedTime(assignedTime);
        return this;
    }

    public void setAssignedTime(Instant assignedTime) {
        this.assignedTime = assignedTime;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public SprintTask duration(Duration duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public SprintTask task(Task task) {
        this.setTask(task);
        return this;
    }

    public Sprint getSprint() {
        return this.sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public SprintTask sprint(Sprint sprint) {
        this.setSprint(sprint);
        return this;
    }

    public Party getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(Party party) {
        this.assignedTo = party;
    }

    public SprintTask assignedTo(Party party) {
        this.setAssignedTo(party);
        return this;
    }

    public Party getAssignedBy() {
        return this.assignedBy;
    }

    public void setAssignedBy(Party party) {
        this.assignedBy = party;
    }

    public SprintTask assignedBy(Party party) {
        this.setAssignedBy(party);
        return this;
    }

    public Party getQa() {
        return this.qa;
    }

    public void setQa(Party party) {
        this.qa = party;
    }

    public SprintTask qa(Party party) {
        this.setQa(party);
        return this;
    }

    public Party getReviewedBy() {
        return this.reviewedBy;
    }

    public void setReviewedBy(Party party) {
        this.reviewedBy = party;
    }

    public SprintTask reviewedBy(Party party) {
        this.setReviewedBy(party);
        return this;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SprintTask status(Status status) {
        this.setStatus(status);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SprintTask)) {
            return false;
        }
        return getId() != null && getId().equals(((SprintTask) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SprintTask{" +
            "id=" + getId() +
            ", sequenceNo=" + getSequenceNo() +
            ", storyPoints=" + getStoryPoints() +
            ", fromTime='" + getFromTime() + "'" +
            ", thruTime='" + getThruTime() + "'" +
            ", assignedTime='" + getAssignedTime() + "'" +
            ", duration='" + getDuration() + "'" +
            "}";
    }
}
