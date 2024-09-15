package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.TaskLog;
import in.beze.pilot.repository.TaskLogRepository;
import in.beze.pilot.service.criteria.TaskLogCriteria;
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
 * Service for executing complex queries for {@link TaskLog} entities in the database.
 * The main input is a {@link TaskLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TaskLog} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TaskLogQueryService extends QueryService<TaskLog> {

    private static final Logger LOG = LoggerFactory.getLogger(TaskLogQueryService.class);

    private final TaskLogRepository taskLogRepository;

    public TaskLogQueryService(TaskLogRepository taskLogRepository) {
        this.taskLogRepository = taskLogRepository;
    }

    /**
     * Return a {@link Page} of {@link TaskLog} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TaskLog> findByCriteria(TaskLogCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TaskLog> specification = createSpecification(criteria);
        return taskLogRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TaskLogCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TaskLog> specification = createSpecification(criteria);
        return taskLogRepository.count(specification);
    }

    /**
     * Function to convert {@link TaskLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TaskLog> createSpecification(TaskLogCriteria criteria) {
        Specification<TaskLog> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TaskLog_.id));
            }
            if (criteria.getComments() != null) {
                specification = specification.and(buildStringSpecification(criteria.getComments(), TaskLog_.comments));
            }
            if (criteria.getFromTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFromTime(), TaskLog_.fromTime));
            }
            if (criteria.getToTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getToTime(), TaskLog_.toTime));
            }
            if (criteria.getTaskId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getTaskId(), root -> root.join(TaskLog_.task, JoinType.LEFT).get(Task_.id))
                );
            }
        }
        return specification;
    }
}
