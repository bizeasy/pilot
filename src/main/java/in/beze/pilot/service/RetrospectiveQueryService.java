package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.Retrospective;
import in.beze.pilot.repository.RetrospectiveRepository;
import in.beze.pilot.service.criteria.RetrospectiveCriteria;
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
 * Service for executing complex queries for {@link Retrospective} entities in the database.
 * The main input is a {@link RetrospectiveCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Retrospective} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RetrospectiveQueryService extends QueryService<Retrospective> {

    private static final Logger LOG = LoggerFactory.getLogger(RetrospectiveQueryService.class);

    private final RetrospectiveRepository retrospectiveRepository;

    public RetrospectiveQueryService(RetrospectiveRepository retrospectiveRepository) {
        this.retrospectiveRepository = retrospectiveRepository;
    }

    /**
     * Return a {@link Page} of {@link Retrospective} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Retrospective> findByCriteria(RetrospectiveCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Retrospective> specification = createSpecification(criteria);
        return retrospectiveRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RetrospectiveCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Retrospective> specification = createSpecification(criteria);
        return retrospectiveRepository.count(specification);
    }

    /**
     * Function to convert {@link RetrospectiveCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Retrospective> createSpecification(RetrospectiveCriteria criteria) {
        Specification<Retrospective> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Retrospective_.id));
            }
            if (criteria.getSummary() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSummary(), Retrospective_.summary));
            }
            if (criteria.getActionItems() != null) {
                specification = specification.and(buildStringSpecification(criteria.getActionItems(), Retrospective_.actionItems));
            }
            if (criteria.getDateCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateCreated(), Retrospective_.dateCreated));
            }
            if (criteria.getSprintId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getSprintId(), root -> root.join(Retrospective_.sprint, JoinType.LEFT).get(Sprint_.id))
                );
            }
        }
        return specification;
    }
}
