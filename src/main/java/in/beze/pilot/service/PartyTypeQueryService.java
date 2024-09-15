package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.PartyType;
import in.beze.pilot.repository.PartyTypeRepository;
import in.beze.pilot.service.criteria.PartyTypeCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PartyType} entities in the database.
 * The main input is a {@link PartyTypeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PartyType} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PartyTypeQueryService extends QueryService<PartyType> {

    private static final Logger LOG = LoggerFactory.getLogger(PartyTypeQueryService.class);

    private final PartyTypeRepository partyTypeRepository;

    public PartyTypeQueryService(PartyTypeRepository partyTypeRepository) {
        this.partyTypeRepository = partyTypeRepository;
    }

    /**
     * Return a {@link Page} of {@link PartyType} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PartyType> findByCriteria(PartyTypeCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PartyType> specification = createSpecification(criteria);
        return partyTypeRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PartyTypeCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PartyType> specification = createSpecification(criteria);
        return partyTypeRepository.count(specification);
    }

    /**
     * Function to convert {@link PartyTypeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PartyType> createSpecification(PartyTypeCriteria criteria) {
        Specification<PartyType> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PartyType_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), PartyType_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), PartyType_.description));
            }
        }
        return specification;
    }
}
