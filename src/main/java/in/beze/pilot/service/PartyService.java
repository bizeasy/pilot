package in.beze.pilot.service;

import in.beze.pilot.domain.Party;
import in.beze.pilot.repository.PartyRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.Party}.
 */
@Service
@Transactional
public class PartyService {

    private static final Logger LOG = LoggerFactory.getLogger(PartyService.class);

    private final PartyRepository partyRepository;

    public PartyService(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    /**
     * Save a party.
     *
     * @param party the entity to save.
     * @return the persisted entity.
     */
    public Party save(Party party) {
        LOG.debug("Request to save Party : {}", party);
        return partyRepository.save(party);
    }

    /**
     * Update a party.
     *
     * @param party the entity to save.
     * @return the persisted entity.
     */
    public Party update(Party party) {
        LOG.debug("Request to update Party : {}", party);
        return partyRepository.save(party);
    }

    /**
     * Partially update a party.
     *
     * @param party the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Party> partialUpdate(Party party) {
        LOG.debug("Request to partially update Party : {}", party);

        return partyRepository
            .findById(party.getId())
            .map(existingParty -> {
                if (party.getFirstName() != null) {
                    existingParty.setFirstName(party.getFirstName());
                }
                if (party.getLastName() != null) {
                    existingParty.setLastName(party.getLastName());
                }
                if (party.getDisplayName() != null) {
                    existingParty.setDisplayName(party.getDisplayName());
                }
                if (party.getEmail() != null) {
                    existingParty.setEmail(party.getEmail());
                }
                if (party.getDob() != null) {
                    existingParty.setDob(party.getDob());
                }
                if (party.getNotes() != null) {
                    existingParty.setNotes(party.getNotes());
                }
                if (party.getMobileNumber() != null) {
                    existingParty.setMobileNumber(party.getMobileNumber());
                }
                if (party.getEmployeeId() != null) {
                    existingParty.setEmployeeId(party.getEmployeeId());
                }
                if (party.getLogin() != null) {
                    existingParty.setLogin(party.getLogin());
                }

                return existingParty;
            })
            .map(partyRepository::save);
    }

    /**
     * Get all the parties with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Party> findAllWithEagerRelationships(Pageable pageable) {
        return partyRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one party by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Party> findOne(Long id) {
        LOG.debug("Request to get Party : {}", id);
        return partyRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the party by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Party : {}", id);
        partyRepository.deleteById(id);
    }
}
