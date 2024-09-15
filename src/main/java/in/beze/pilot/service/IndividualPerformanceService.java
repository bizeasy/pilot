package in.beze.pilot.service;

import in.beze.pilot.domain.IndividualPerformance;
import in.beze.pilot.repository.IndividualPerformanceRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.IndividualPerformance}.
 */
@Service
@Transactional
public class IndividualPerformanceService {

    private static final Logger LOG = LoggerFactory.getLogger(IndividualPerformanceService.class);

    private final IndividualPerformanceRepository individualPerformanceRepository;

    public IndividualPerformanceService(IndividualPerformanceRepository individualPerformanceRepository) {
        this.individualPerformanceRepository = individualPerformanceRepository;
    }

    /**
     * Save a individualPerformance.
     *
     * @param individualPerformance the entity to save.
     * @return the persisted entity.
     */
    public IndividualPerformance save(IndividualPerformance individualPerformance) {
        LOG.debug("Request to save IndividualPerformance : {}", individualPerformance);
        return individualPerformanceRepository.save(individualPerformance);
    }

    /**
     * Update a individualPerformance.
     *
     * @param individualPerformance the entity to save.
     * @return the persisted entity.
     */
    public IndividualPerformance update(IndividualPerformance individualPerformance) {
        LOG.debug("Request to update IndividualPerformance : {}", individualPerformance);
        return individualPerformanceRepository.save(individualPerformance);
    }

    /**
     * Partially update a individualPerformance.
     *
     * @param individualPerformance the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<IndividualPerformance> partialUpdate(IndividualPerformance individualPerformance) {
        LOG.debug("Request to partially update IndividualPerformance : {}", individualPerformance);

        return individualPerformanceRepository
            .findById(individualPerformance.getId())
            .map(existingIndividualPerformance -> {
                if (individualPerformance.getCompletedTasks() != null) {
                    existingIndividualPerformance.setCompletedTasks(individualPerformance.getCompletedTasks());
                }
                if (individualPerformance.getVelocity() != null) {
                    existingIndividualPerformance.setVelocity(individualPerformance.getVelocity());
                }
                if (individualPerformance.getStoryPointsCompleted() != null) {
                    existingIndividualPerformance.setStoryPointsCompleted(individualPerformance.getStoryPointsCompleted());
                }

                return existingIndividualPerformance;
            })
            .map(individualPerformanceRepository::save);
    }

    /**
     * Get all the individualPerformances with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<IndividualPerformance> findAllWithEagerRelationships(Pageable pageable) {
        return individualPerformanceRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one individualPerformance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<IndividualPerformance> findOne(Long id) {
        LOG.debug("Request to get IndividualPerformance : {}", id);
        return individualPerformanceRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the individualPerformance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete IndividualPerformance : {}", id);
        individualPerformanceRepository.deleteById(id);
    }
}
