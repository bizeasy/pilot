package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.SprintTaskHistory;
import in.beze.pilot.repository.SprintTaskHistoryRepository;
import in.beze.pilot.service.criteria.SprintTaskHistoryCriteria;
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
 * Service for executing complex queries for {@link SprintTaskHistory} entities in the database.
 * The main input is a {@link SprintTaskHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SprintTaskHistory} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SprintTaskHistoryQueryService extends QueryService<SprintTaskHistory> {

    private static final Logger LOG = LoggerFactory.getLogger(SprintTaskHistoryQueryService.class);

    private final SprintTaskHistoryRepository sprintTaskHistoryRepository;

    public SprintTaskHistoryQueryService(SprintTaskHistoryRepository sprintTaskHistoryRepository) {
        this.sprintTaskHistoryRepository = sprintTaskHistoryRepository;
    }

    /**
     * Return a {@link Page} of {@link SprintTaskHistory} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SprintTaskHistory> findByCriteria(SprintTaskHistoryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SprintTaskHistory> specification = createSpecification(criteria);
        return sprintTaskHistoryRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SprintTaskHistoryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SprintTaskHistory> specification = createSpecification(criteria);
        return sprintTaskHistoryRepository.count(specification);
    }

    /**
     * Function to convert {@link SprintTaskHistoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SprintTaskHistory> createSpecification(SprintTaskHistoryCriteria criteria) {
        Specification<SprintTaskHistory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SprintTaskHistory_.id));
            }
            if (criteria.getComments() != null) {
                specification = specification.and(buildStringSpecification(criteria.getComments(), SprintTaskHistory_.comments));
            }
            if (criteria.getFromDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFromDate(), SprintTaskHistory_.fromDate));
            }
            if (criteria.getToDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getToDate(), SprintTaskHistory_.toDate));
            }
            if (criteria.getFromStatusId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getFromStatusId(), root ->
                        root.join(SprintTaskHistory_.fromStatus, JoinType.LEFT).get(Status_.id)
                    )
                );
            }
            if (criteria.getToStatusId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getToStatusId(), root ->
                        root.join(SprintTaskHistory_.toStatus, JoinType.LEFT).get(Status_.id)
                    )
                );
            }
        }
        return specification;
    }
}
