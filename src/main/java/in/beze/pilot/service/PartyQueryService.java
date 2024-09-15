package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.Party;
import in.beze.pilot.repository.PartyRepository;
import in.beze.pilot.service.criteria.PartyCriteria;
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
 * Service for executing complex queries for {@link Party} entities in the database.
 * The main input is a {@link PartyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Party} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PartyQueryService extends QueryService<Party> {

    private static final Logger LOG = LoggerFactory.getLogger(PartyQueryService.class);

    private final PartyRepository partyRepository;

    public PartyQueryService(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    /**
     * Return a {@link Page} of {@link Party} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Party> findByCriteria(PartyCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Party> specification = createSpecification(criteria);
        return partyRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PartyCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Party> specification = createSpecification(criteria);
        return partyRepository.count(specification);
    }

    /**
     * Function to convert {@link PartyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Party> createSpecification(PartyCriteria criteria) {
        Specification<Party> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Party_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), Party_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), Party_.lastName));
            }
            if (criteria.getDisplayName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDisplayName(), Party_.displayName));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Party_.email));
            }
            if (criteria.getDob() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDob(), Party_.dob));
            }
            if (criteria.getNotes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNotes(), Party_.notes));
            }
            if (criteria.getMobileNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMobileNumber(), Party_.mobileNumber));
            }
            if (criteria.getEmployeeId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmployeeId(), Party_.employeeId));
            }
            if (criteria.getLogin() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLogin(), Party_.login));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(Party_.user, JoinType.LEFT).get(User_.id))
                );
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getStatusId(), root -> root.join(Party_.status, JoinType.LEFT).get(Status_.id))
                );
            }
            if (criteria.getPartyTypeId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getPartyTypeId(), root -> root.join(Party_.partyType, JoinType.LEFT).get(PartyType_.id))
                );
            }
        }
        return specification;
    }
}
