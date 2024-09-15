package in.beze.pilot.service;

import in.beze.pilot.domain.Retrospective;
import in.beze.pilot.repository.RetrospectiveRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.Retrospective}.
 */
@Service
@Transactional
public class RetrospectiveService {

    private static final Logger LOG = LoggerFactory.getLogger(RetrospectiveService.class);

    private final RetrospectiveRepository retrospectiveRepository;

    public RetrospectiveService(RetrospectiveRepository retrospectiveRepository) {
        this.retrospectiveRepository = retrospectiveRepository;
    }

    /**
     * Save a retrospective.
     *
     * @param retrospective the entity to save.
     * @return the persisted entity.
     */
    public Retrospective save(Retrospective retrospective) {
        LOG.debug("Request to save Retrospective : {}", retrospective);
        return retrospectiveRepository.save(retrospective);
    }

    /**
     * Update a retrospective.
     *
     * @param retrospective the entity to save.
     * @return the persisted entity.
     */
    public Retrospective update(Retrospective retrospective) {
        LOG.debug("Request to update Retrospective : {}", retrospective);
        return retrospectiveRepository.save(retrospective);
    }

    /**
     * Partially update a retrospective.
     *
     * @param retrospective the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Retrospective> partialUpdate(Retrospective retrospective) {
        LOG.debug("Request to partially update Retrospective : {}", retrospective);

        return retrospectiveRepository
            .findById(retrospective.getId())
            .map(existingRetrospective -> {
                if (retrospective.getSummary() != null) {
                    existingRetrospective.setSummary(retrospective.getSummary());
                }
                if (retrospective.getActionItems() != null) {
                    existingRetrospective.setActionItems(retrospective.getActionItems());
                }
                if (retrospective.getDateCreated() != null) {
                    existingRetrospective.setDateCreated(retrospective.getDateCreated());
                }

                return existingRetrospective;
            })
            .map(retrospectiveRepository::save);
    }

    /**
     * Get all the retrospectives with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Retrospective> findAllWithEagerRelationships(Pageable pageable) {
        return retrospectiveRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one retrospective by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Retrospective> findOne(Long id) {
        LOG.debug("Request to get Retrospective : {}", id);
        return retrospectiveRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the retrospective by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Retrospective : {}", id);
        retrospectiveRepository.deleteById(id);
    }
}
