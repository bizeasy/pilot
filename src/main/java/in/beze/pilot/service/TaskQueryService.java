package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.Task;
import in.beze.pilot.repository.TaskRepository;
import in.beze.pilot.service.criteria.TaskCriteria;
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
 * Service for executing complex queries for {@link Task} entities in the database.
 * The main input is a {@link TaskCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Task} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TaskQueryService extends QueryService<Task> {

    private static final Logger LOG = LoggerFactory.getLogger(TaskQueryService.class);

    private final TaskRepository taskRepository;

    public TaskQueryService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Return a {@link Page} of {@link Task} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Task> findByCriteria(TaskCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Task> specification = createSpecification(criteria);
        return taskRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TaskCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Task> specification = createSpecification(criteria);
        return taskRepository.count(specification);
    }

    /**
     * Function to convert {@link TaskCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Task> createSpecification(TaskCriteria criteria) {
        Specification<Task> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Task_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Task_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Task_.description));
            }
            if (criteria.getPriority() != null) {
                specification = specification.and(buildSpecification(criteria.getPriority(), Task_.priority));
            }
            if (criteria.getDueDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDueDate(), Task_.dueDate));
            }
            if (criteria.getStoryPoints() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStoryPoints(), Task_.storyPoints));
            }
            if (criteria.getStartTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartTime(), Task_.startTime));
            }
            if (criteria.getPauseTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPauseTime(), Task_.pauseTime));
            }
            if (criteria.getEndTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndTime(), Task_.endTime));
            }
            if (criteria.getTotalDuration() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalDuration(), Task_.totalDuration));
            }
            if (criteria.getSequenceNo() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSequenceNo(), Task_.sequenceNo));
            }
            if (criteria.getCommentsId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCommentsId(), root -> root.join(Task_.comments, JoinType.LEFT).get(Comment_.id))
                );
            }
            if (criteria.getProjectId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getProjectId(), root -> root.join(Task_.project, JoinType.LEFT).get(Project_.id))
                );
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getStatusId(), root -> root.join(Task_.status, JoinType.LEFT).get(Status_.id))
                );
            }
            if (criteria.getAssigneeId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAssigneeId(), root -> root.join(Task_.assignee, JoinType.LEFT).get(Party_.id))
                );
            }
        }
        return specification;
    }
}
