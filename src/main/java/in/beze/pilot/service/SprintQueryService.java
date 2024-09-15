package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.Sprint;
import in.beze.pilot.repository.SprintRepository;
import in.beze.pilot.service.criteria.SprintCriteria;
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
 * Service for executing complex queries for {@link Sprint} entities in the database.
 * The main input is a {@link SprintCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Sprint} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SprintQueryService extends QueryService<Sprint> {

    private static final Logger LOG = LoggerFactory.getLogger(SprintQueryService.class);

    private final SprintRepository sprintRepository;

    public SprintQueryService(SprintRepository sprintRepository) {
        this.sprintRepository = sprintRepository;
    }

    /**
     * Return a {@link Page} of {@link Sprint} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Sprint> findByCriteria(SprintCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Sprint> specification = createSpecification(criteria);
        return sprintRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SprintCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Sprint> specification = createSpecification(criteria);
        return sprintRepository.count(specification);
    }

    /**
     * Function to convert {@link SprintCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Sprint> createSpecification(SprintCriteria criteria) {
        Specification<Sprint> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Sprint_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Sprint_.name));
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), Sprint_.startDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), Sprint_.endDate));
            }
            if (criteria.getGoal() != null) {
                specification = specification.and(buildStringSpecification(criteria.getGoal(), Sprint_.goal));
            }
            if (criteria.getTotalPoints() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalPoints(), Sprint_.totalPoints));
            }
            if (criteria.getProjectId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getProjectId(), root -> root.join(Sprint_.project, JoinType.LEFT).get(Project_.id))
                );
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getStatusId(), root -> root.join(Sprint_.status, JoinType.LEFT).get(Status_.id))
                );
            }
        }
        return specification;
    }
}
