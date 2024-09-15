package in.beze.pilot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A IndividualPerformance.
 */
@Entity
@Table(name = "individual_performance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IndividualPerformance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "completed_tasks")
    private Integer completedTasks;

    @Column(name = "velocity")
    private Integer velocity;

    @Column(name = "story_points_completed")
    private Integer storyPointsCompleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "status", "partyType" }, allowSetters = true)
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "project", "status" }, allowSetters = true)
    private Sprint sprint;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public IndividualPerformance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCompletedTasks() {
        return this.completedTasks;
    }

    public IndividualPerformance completedTasks(Integer completedTasks) {
        this.setCompletedTasks(completedTasks);
        return this;
    }

    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Integer getVelocity() {
        return this.velocity;
    }

    public IndividualPerformance velocity(Integer velocity) {
        this.setVelocity(velocity);
        return this;
    }

    public void setVelocity(Integer velocity) {
        this.velocity = velocity;
    }

    public Integer getStoryPointsCompleted() {
        return this.storyPointsCompleted;
    }

    public IndividualPerformance storyPointsCompleted(Integer storyPointsCompleted) {
        this.setStoryPointsCompleted(storyPointsCompleted);
        return this;
    }

    public void setStoryPointsCompleted(Integer storyPointsCompleted) {
        this.storyPointsCompleted = storyPointsCompleted;
    }

    public Party getParty() {
        return this.party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public IndividualPerformance party(Party party) {
        this.setParty(party);
        return this;
    }

    public Sprint getSprint() {
        return this.sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public IndividualPerformance sprint(Sprint sprint) {
        this.setSprint(sprint);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IndividualPerformance)) {
            return false;
        }
        return getId() != null && getId().equals(((IndividualPerformance) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IndividualPerformance{" +
            "id=" + getId() +
            ", completedTasks=" + getCompletedTasks() +
            ", velocity=" + getVelocity() +
            ", storyPointsCompleted=" + getStoryPointsCompleted() +
            "}";
    }
}
