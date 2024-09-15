package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.TaskHistory;
import in.beze.pilot.repository.TaskHistoryRepository;
import in.beze.pilot.service.criteria.TaskHistoryCriteria;
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
 * Service for executing complex queries for {@link TaskHistory} entities in the database.
 * The main input is a {@link TaskHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TaskHistory} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TaskHistoryQueryService extends QueryService<TaskHistory> {

    private static final Logger LOG = LoggerFactory.getLogger(TaskHistoryQueryService.class);

    private final TaskHistoryRepository taskHistoryRepository;

    public TaskHistoryQueryService(TaskHistoryRepository taskHistoryRepository) {
        this.taskHistoryRepository = taskHistoryRepository;
    }

    /**
     * Return a {@link Page} of {@link TaskHistory} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TaskHistory> findByCriteria(TaskHistoryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TaskHistory> specification = createSpecification(criteria);
        return taskHistoryRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TaskHistoryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TaskHistory> specification = createSpecification(criteria);
        return taskHistoryRepository.count(specification);
    }

    /**
     * Function to convert {@link TaskHistoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TaskHistory> createSpecification(TaskHistoryCriteria criteria) {
        Specification<TaskHistory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TaskHistory_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), TaskHistory_.type));
            }
            if (criteria.getAssignedToId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAssignedToId(), root -> root.join(TaskHistory_.assignedTo, JoinType.LEFT).get(Party_.id))
                );
            }
            if (criteria.getSprintId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getSprintId(), root -> root.join(TaskHistory_.sprint, JoinType.LEFT).get(Sprint_.id))
                );
            }
            if (criteria.getAssignedById() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAssignedById(), root -> root.join(TaskHistory_.assignedBy, JoinType.LEFT).get(Party_.id))
                );
            }
        }
        return specification;
    }
}
