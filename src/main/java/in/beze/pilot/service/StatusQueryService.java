package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.Status;
import in.beze.pilot.repository.StatusRepository;
import in.beze.pilot.service.criteria.StatusCriteria;
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
 * Service for executing complex queries for {@link Status} entities in the database.
 * The main input is a {@link StatusCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Status} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StatusQueryService extends QueryService<Status> {

    private static final Logger LOG = LoggerFactory.getLogger(StatusQueryService.class);

    private final StatusRepository statusRepository;

    public StatusQueryService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    /**
     * Return a {@link Page} of {@link Status} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Status> findByCriteria(StatusCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Status> specification = createSpecification(criteria);
        return statusRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StatusCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Status> specification = createSpecification(criteria);
        return statusRepository.count(specification);
    }

    /**
     * Function to convert {@link StatusCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Status> createSpecification(StatusCriteria criteria) {
        Specification<Status> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Status_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Status_.name));
            }
            if (criteria.getSequenceNo() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSequenceNo(), Status_.sequenceNo));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Status_.description));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), Status_.type));
            }
            if (criteria.getCategoryId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCategoryId(), root -> root.join(Status_.category, JoinType.LEFT).get(StatusCategory_.id))
                );
            }
        }
        return specification;
    }
}
