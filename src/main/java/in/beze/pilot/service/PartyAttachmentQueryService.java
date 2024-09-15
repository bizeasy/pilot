package in.beze.pilot.service;

import in.beze.pilot.domain.*; // for static metamodels
import in.beze.pilot.domain.PartyAttachment;
import in.beze.pilot.repository.PartyAttachmentRepository;
import in.beze.pilot.service.criteria.PartyAttachmentCriteria;
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
 * Service for executing complex queries for {@link PartyAttachment} entities in the database.
 * The main input is a {@link PartyAttachmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PartyAttachment} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PartyAttachmentQueryService extends QueryService<PartyAttachment> {

    private static final Logger LOG = LoggerFactory.getLogger(PartyAttachmentQueryService.class);

    private final PartyAttachmentRepository partyAttachmentRepository;

    public PartyAttachmentQueryService(PartyAttachmentRepository partyAttachmentRepository) {
        this.partyAttachmentRepository = partyAttachmentRepository;
    }

    /**
     * Return a {@link Page} of {@link PartyAttachment} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PartyAttachment> findByCriteria(PartyAttachmentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PartyAttachment> specification = createSpecification(criteria);
        return partyAttachmentRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PartyAttachmentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PartyAttachment> specification = createSpecification(criteria);
        return partyAttachmentRepository.count(specification);
    }

    /**
     * Function to convert {@link PartyAttachmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PartyAttachment> createSpecification(PartyAttachmentCriteria criteria) {
        Specification<PartyAttachment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PartyAttachment_.id));
            }
            if (criteria.getPartyId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getPartyId(), root -> root.join(PartyAttachment_.party, JoinType.LEFT).get(Party_.id))
                );
            }
            if (criteria.getAttachmentId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAttachmentId(), root ->
                        root.join(PartyAttachment_.attachment, JoinType.LEFT).get(Attachment_.id)
                    )
                );
            }
        }
        return specification;
    }
}
