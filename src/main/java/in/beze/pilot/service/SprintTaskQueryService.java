package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.SprintTask;
import in.beze.pilot.repository.SprintTaskRepository;
import in.beze.pilot.service.criteria.SprintTaskCriteria;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SprintTask} entities in the database.
 * The main input is a {@link SprintTaskCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SprintTask} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SprintTaskQueryService extends QueryService<SprintTask> {

    private static final Logger LOG = LoggerFactory.getLogger(SprintTaskQueryService.class);

    private final SprintTaskRepository sprintTaskRepository;

    public SprintTaskQueryService(SprintTaskRepository sprintTaskRepository) {
        this.sprintTaskRepository = sprintTaskRepository;
    }

    /**
     * Return a {@link Page} of {@link SprintTask} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SprintTask> findByCriteria(SprintTaskCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SprintTask> specification = createSpecification(criteria);
        return sprintTaskRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SprintTaskCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SprintTask> specification = createSpecification(criteria);
        return sprintTaskRepository.count(specification);
    }

    /**
     * Function to convert {@link SprintTaskCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SprintTask> createSpecification(SprintTaskCriteria criteria) {
        Specification<SprintTask> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SprintTask_.id));
            }
            if (criteria.getSequenceNo() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSequenceNo(), SprintTask_.sequenceNo));
            }
            if (criteria.getStoryPoints() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStoryPoints(), SprintTask_.storyPoints));
            }
            if (criteria.getFromTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFromTime(), SprintTask_.fromTime));
            }
            if (criteria.getThruTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getThruTime(), SprintTask_.thruTime));
            }
            if (criteria.getAssignedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAssignedTime(), SprintTask_.assignedTime));
            }
            if (criteria.getDuration() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDuration(), SprintTask_.duration));
            }
            if (criteria.getTaskId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getTaskId(), root -> root.join(SprintTask_.task, JoinType.LEFT).get(Task_.id))
                );
            }
            if (criteria.getSprintId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getSprintId(), root -> root.join(SprintTask_.sprint, JoinType.LEFT).get(Sprint_.id))
                );
            }
            if (criteria.getAssignedToId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAssignedToId(), root -> root.join(SprintTask_.assignedTo, JoinType.LEFT).get(Party_.id))
                );
            }
            if (criteria.getAssignedById() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAssignedById(), root -> root.join(SprintTask_.assignedBy, JoinType.LEFT).get(Party_.id))
                );
            }
            if (criteria.getQaId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getQaId(), root -> root.join(SprintTask_.qa, JoinType.LEFT).get(Party_.id))
                );
            }
            if (criteria.getReviewedById() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getReviewedById(), root -> root.join(SprintTask_.reviewedBy, JoinType.LEFT).get(Party_.id))
                );
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getStatusId(), root -> root.join(SprintTask_.status, JoinType.LEFT).get(Status_.id))
                );
            }
        }
        return specification;
    }
}
