package in.beze.pilot.service;

import in.beze.pilot.domain.SprintTask;
import in.beze.pilot.repository.SprintTaskRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.SprintTask}.
 */
@Service
@Transactional
public class SprintTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(SprintTaskService.class);

    private final SprintTaskRepository sprintTaskRepository;

    public SprintTaskService(SprintTaskRepository sprintTaskRepository) {
        this.sprintTaskRepository = sprintTaskRepository;
    }

    /**
     * Save a sprintTask.
     *
     * @param sprintTask the entity to save.
     * @return the persisted entity.
     */
    public SprintTask save(SprintTask sprintTask) {
        LOG.debug("Request to save SprintTask : {}", sprintTask);
        return sprintTaskRepository.save(sprintTask);
    }

    /**
     * Update a sprintTask.
     *
     * @param sprintTask the entity to save.
     * @return the persisted entity.
     */
    public SprintTask update(SprintTask sprintTask) {
        LOG.debug("Request to update SprintTask : {}", sprintTask);
        return sprintTaskRepository.save(sprintTask);
    }

    /**
     * Partially update a sprintTask.
     *
     * @param sprintTask the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SprintTask> partialUpdate(SprintTask sprintTask) {
        LOG.debug("Request to partially update SprintTask : {}", sprintTask);

        return sprintTaskRepository
            .findById(sprintTask.getId())
            .map(existingSprintTask -> {
                if (sprintTask.getSequenceNo() != null) {
                    existingSprintTask.setSequenceNo(sprintTask.getSequenceNo());
                }
                if (sprintTask.getStoryPoints() != null) {
                    existingSprintTask.setStoryPoints(sprintTask.getStoryPoints());
                }
                if (sprintTask.getFromTime() != null) {
                    existingSprintTask.setFromTime(sprintTask.getFromTime());
                }
                if (sprintTask.getThruTime() != null) {
                    existingSprintTask.setThruTime(sprintTask.getThruTime());
                }
                if (sprintTask.getAssignedTime() != null) {
                    existingSprintTask.setAssignedTime(sprintTask.getAssignedTime());
                }
                if (sprintTask.getDuration() != null) {
                    existingSprintTask.setDuration(sprintTask.getDuration());
                }

                return existingSprintTask;
            })
            .map(sprintTaskRepository::save);
    }

    /**
     * Get all the sprintTasks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SprintTask> findAllWithEagerRelationships(Pageable pageable) {
        return sprintTaskRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one sprintTask by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SprintTask> findOne(Long id) {
        LOG.debug("Request to get SprintTask : {}", id);
        return sprintTaskRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the sprintTask by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SprintTask : {}", id);
        sprintTaskRepository.deleteById(id);
    }
}
