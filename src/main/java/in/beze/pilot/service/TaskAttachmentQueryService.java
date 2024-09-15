package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.TaskAttachment;
import in.beze.pilot.repository.TaskAttachmentRepository;
import in.beze.pilot.service.criteria.TaskAttachmentCriteria;
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
 * Service for executing complex queries for {@link TaskAttachment} entities in the database.
 * The main input is a {@link TaskAttachmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TaskAttachment} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TaskAttachmentQueryService extends QueryService<TaskAttachment> {

    private static final Logger LOG = LoggerFactory.getLogger(TaskAttachmentQueryService.class);

    private final TaskAttachmentRepository taskAttachmentRepository;

    public TaskAttachmentQueryService(TaskAttachmentRepository taskAttachmentRepository) {
        this.taskAttachmentRepository = taskAttachmentRepository;
    }

    /**
     * Return a {@link Page} of {@link TaskAttachment} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TaskAttachment> findByCriteria(TaskAttachmentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TaskAttachment> specification = createSpecification(criteria);
        return taskAttachmentRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TaskAttachmentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TaskAttachment> specification = createSpecification(criteria);
        return taskAttachmentRepository.count(specification);
    }

    /**
     * Function to convert {@link TaskAttachmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TaskAttachment> createSpecification(TaskAttachmentCriteria criteria) {
        Specification<TaskAttachment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TaskAttachment_.id));
            }
            if (criteria.getTaskId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getTaskId(), root -> root.join(TaskAttachment_.task, JoinType.LEFT).get(Task_.id))
                );
            }
            if (criteria.getAttachmentId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAttachmentId(), root ->
                        root.join(TaskAttachment_.attachment, JoinType.LEFT).get(Attachment_.id)
                    )
                );
            }
        }
        return specification;
    }
}
