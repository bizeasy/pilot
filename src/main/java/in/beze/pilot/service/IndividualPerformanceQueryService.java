package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.IndividualPerformance;
import in.beze.pilot.repository.IndividualPerformanceRepository;
import in.beze.pilot.service.criteria.IndividualPerformanceCriteria;
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
 * Service for executing complex queries for {@link IndividualPerformance} entities in the database.
 * The main input is a {@link IndividualPerformanceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link IndividualPerformance} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class IndividualPerformanceQueryService extends QueryService<IndividualPerformance> {

    private static final Logger LOG = LoggerFactory.getLogger(IndividualPerformanceQueryService.class);

    private final IndividualPerformanceRepository individualPerformanceRepository;

    public IndividualPerformanceQueryService(IndividualPerformanceRepository individualPerformanceRepository) {
        this.individualPerformanceRepository = individualPerformanceRepository;
    }

    /**
     * Return a {@link Page} of {@link IndividualPerformance} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<IndividualPerformance> findByCriteria(IndividualPerformanceCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<IndividualPerformance> specification = createSpecification(criteria);
        return individualPerformanceRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(IndividualPerformanceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<IndividualPerformance> specification = createSpecification(criteria);
        return individualPerformanceRepository.count(specification);
    }

    /**
     * Function to convert {@link IndividualPerformanceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<IndividualPerformance> createSpecification(IndividualPerformanceCriteria criteria) {
        Specification<IndividualPerformance> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), IndividualPerformance_.id));
            }
            if (criteria.getCompletedTasks() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getCompletedTasks(), IndividualPerformance_.completedTasks)
                );
            }
            if (criteria.getVelocity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVelocity(), IndividualPerformance_.velocity));
            }
            if (criteria.getStoryPointsCompleted() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getStoryPointsCompleted(), IndividualPerformance_.storyPointsCompleted)
                );
            }
            if (criteria.getPartyId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getPartyId(), root -> root.join(IndividualPerformance_.party, JoinType.LEFT).get(Party_.id))
                );
            }
            if (criteria.getSprintId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getSprintId(), root ->
                        root.join(IndividualPerformance_.sprint, JoinType.LEFT).get(Sprint_.id)
                    )
                );
            }
        }
        return specification;
    }
}
