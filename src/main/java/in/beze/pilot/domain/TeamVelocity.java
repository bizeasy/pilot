package in.beze.pilot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TeamVelocity.
 */
@Entity
@Table(name = "team_velocity")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamVelocity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "sprint_velocity", nullable = false)
    private Integer sprintVelocity;

    @NotNull
    @Column(name = "average_velocity", nullable = false)
    private Integer averageVelocity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "project", "status" }, allowSetters = true)
    private Sprint sprint;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TeamVelocity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSprintVelocity() {
        return this.sprintVelocity;
    }

    public TeamVelocity sprintVelocity(Integer sprintVelocity) {
        this.setSprintVelocity(sprintVelocity);
        return this;
    }

    public void setSprintVelocity(Integer sprintVelocity) {
        this.sprintVelocity = sprintVelocity;
    }

    public Integer getAverageVelocity() {
        return this.averageVelocity;
    }

    public TeamVelocity averageVelocity(Integer averageVelocity) {
        this.setAverageVelocity(averageVelocity);
        return this;
    }

    public void setAverageVelocity(Integer averageVelocity) {
        this.averageVelocity = averageVelocity;
    }

    public Sprint getSprint() {
        return this.sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public TeamVelocity sprint(Sprint sprint) {
        this.setSprint(sprint);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamVelocity)) {
            return false;
        }
        return getId() != null && getId().equals(((TeamVelocity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamVelocity{" +
            "id=" + getId() +
            ", sprintVelocity=" + getSprintVelocity() +
            ", averageVelocity=" + getAverageVelocity() +
            "}";
    }
}
