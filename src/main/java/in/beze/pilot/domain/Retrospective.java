package in.beze.pilot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Retrospective.
 */
@Entity
@Table(name = "retrospective")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Retrospective implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "summary")
    private String summary;

    @Column(name = "action_items")
    private String actionItems;

    @NotNull
    @Column(name = "date_created", nullable = false)
    private Instant dateCreated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "project", "status" }, allowSetters = true)
    private Sprint sprint;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Retrospective id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSummary() {
        return this.summary;
    }

    public Retrospective summary(String summary) {
        this.setSummary(summary);
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getActionItems() {
        return this.actionItems;
    }

    public Retrospective actionItems(String actionItems) {
        this.setActionItems(actionItems);
        return this;
    }

    public void setActionItems(String actionItems) {
        this.actionItems = actionItems;
    }

    public Instant getDateCreated() {
        return this.dateCreated;
    }

    public Retrospective dateCreated(Instant dateCreated) {
        this.setDateCreated(dateCreated);
        return this;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Sprint getSprint() {
        return this.sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public Retrospective sprint(Sprint sprint) {
        this.setSprint(sprint);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Retrospective)) {
            return false;
        }
        return getId() != null && getId().equals(((Retrospective) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Retrospective{" +
            "id=" + getId() +
            ", summary='" + getSummary() + "'" +
            ", actionItems='" + getActionItems() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            "}";
    }
}
