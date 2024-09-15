package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.ProjectAttachment;
import in.beze.pilot.repository.ProjectAttachmentRepository;
import in.beze.pilot.service.criteria.ProjectAttachmentCriteria;
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
 * Service for executing complex queries for {@link ProjectAttachment} entities in the database.
 * The main input is a {@link ProjectAttachmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ProjectAttachment} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectAttachmentQueryService extends QueryService<ProjectAttachment> {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectAttachmentQueryService.class);

    private final ProjectAttachmentRepository projectAttachmentRepository;

    public ProjectAttachmentQueryService(ProjectAttachmentRepository projectAttachmentRepository) {
        this.projectAttachmentRepository = projectAttachmentRepository;
    }

    /**
     * Return a {@link Page} of {@link ProjectAttachment} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectAttachment> findByCriteria(ProjectAttachmentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProjectAttachment> specification = createSpecification(criteria);
        return projectAttachmentRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectAttachmentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ProjectAttachment> specification = createSpecification(criteria);
        return projectAttachmentRepository.count(specification);
    }

    /**
     * Function to convert {@link ProjectAttachmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProjectAttachment> createSpecification(ProjectAttachmentCriteria criteria) {
        Specification<ProjectAttachment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProjectAttachment_.id));
            }
            if (criteria.getFacilityId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getFacilityId(), root ->
                        root.join(ProjectAttachment_.facility, JoinType.LEFT).get(Project_.id)
                    )
                );
            }
            if (criteria.getAttachmentId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAttachmentId(), root ->
                        root.join(ProjectAttachment_.attachment, JoinType.LEFT).get(Attachment_.id)
                    )
                );
            }
        }
        return specification;
    }
}
