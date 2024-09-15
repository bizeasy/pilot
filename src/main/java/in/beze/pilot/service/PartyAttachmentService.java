package in.beze.pilot.service;

import in.beze.pilot.domain.PartyAttachment;
import in.beze.pilot.repository.PartyAttachmentRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.PartyAttachment}.
 */
@Service
@Transactional
public class PartyAttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(PartyAttachmentService.class);

    private final PartyAttachmentRepository partyAttachmentRepository;

    public PartyAttachmentService(PartyAttachmentRepository partyAttachmentRepository) {
        this.partyAttachmentRepository = partyAttachmentRepository;
    }

    /**
     * Save a partyAttachment.
     *
     * @param partyAttachment the entity to save.
     * @return the persisted entity.
     */
    public PartyAttachment save(PartyAttachment partyAttachment) {
        LOG.debug("Request to save PartyAttachment : {}", partyAttachment);
        return partyAttachmentRepository.save(partyAttachment);
    }

    /**
     * Update a partyAttachment.
     *
     * @param partyAttachment the entity to save.
     * @return the persisted entity.
     */
    public PartyAttachment update(PartyAttachment partyAttachment) {
        LOG.debug("Request to update PartyAttachment : {}", partyAttachment);
        return partyAttachmentRepository.save(partyAttachment);
    }

    /**
     * Partially update a partyAttachment.
     *
     * @param partyAttachment the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PartyAttachment> partialUpdate(PartyAttachment partyAttachment) {
        LOG.debug("Request to partially update PartyAttachment : {}", partyAttachment);

        return partyAttachmentRepository.findById(partyAttachment.getId()).map(partyAttachmentRepository::save);
    }

    /**
     * Get all the partyAttachments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PartyAttachment> findAllWithEagerRelationships(Pageable pageable) {
        return partyAttachmentRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one partyAttachment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PartyAttachment> findOne(Long id) {
        LOG.debug("Request to get PartyAttachment : {}", id);
        return partyAttachmentRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the partyAttachment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PartyAttachment : {}", id);
        partyAttachmentRepository.deleteById(id);
    }
}
