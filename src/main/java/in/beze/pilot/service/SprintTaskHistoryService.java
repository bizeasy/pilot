package in.beze.pilot.service;

import in.beze.pilot.domain.SprintTaskHistory;
import in.beze.pilot.repository.SprintTaskHistoryRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.SprintTaskHistory}.
 */
@Service
@Transactional
public class SprintTaskHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(SprintTaskHistoryService.class);

    private final SprintTaskHistoryRepository sprintTaskHistoryRepository;

    public SprintTaskHistoryService(SprintTaskHistoryRepository sprintTaskHistoryRepository) {
        this.sprintTaskHistoryRepository = sprintTaskHistoryRepository;
    }

    /**
     * Save a sprintTaskHistory.
     *
     * @param sprintTaskHistory the entity to save.
     * @return the persisted entity.
     */
    public SprintTaskHistory save(SprintTaskHistory sprintTaskHistory) {
        LOG.debug("Request to save SprintTaskHistory : {}", sprintTaskHistory);
        return sprintTaskHistoryRepository.save(sprintTaskHistory);
    }

    /**
     * Update a sprintTaskHistory.
     *
     * @param sprintTaskHistory the entity to save.
     * @return the persisted entity.
     */
    public SprintTaskHistory update(SprintTaskHistory sprintTaskHistory) {
        LOG.debug("Request to update SprintTaskHistory : {}", sprintTaskHistory);
        return sprintTaskHistoryRepository.save(sprintTaskHistory);
    }

    /**
     * Partially update a sprintTaskHistory.
     *
     * @param sprintTaskHistory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SprintTaskHistory> partialUpdate(SprintTaskHistory sprintTaskHistory) {
        LOG.debug("Request to partially update SprintTaskHistory : {}", sprintTaskHistory);

        return sprintTaskHistoryRepository
            .findById(sprintTaskHistory.getId())
            .map(existingSprintTaskHistory -> {
                if (sprintTaskHistory.getComments() != null) {
                    existingSprintTaskHistory.setComments(sprintTaskHistory.getComments());
                }
                if (sprintTaskHistory.getFromDate() != null) {
                    existingSprintTaskHistory.setFromDate(sprintTaskHistory.getFromDate());
                }
                if (sprintTaskHistory.getToDate() != null) {
                    existingSprintTaskHistory.setToDate(sprintTaskHistory.getToDate());
                }

                return existingSprintTaskHistory;
            })
            .map(sprintTaskHistoryRepository::save);
    }

    /**
     * Get all the sprintTaskHistories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SprintTaskHistory> findAllWithEagerRelationships(Pageable pageable) {
        return sprintTaskHistoryRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one sprintTaskHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SprintTaskHistory> findOne(Long id) {
        LOG.debug("Request to get SprintTaskHistory : {}", id);
        return sprintTaskHistoryRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the sprintTaskHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SprintTaskHistory : {}", id);
        sprintTaskHistoryRepository.deleteById(id);
    }
}
