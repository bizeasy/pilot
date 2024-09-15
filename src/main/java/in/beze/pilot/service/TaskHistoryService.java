package in.beze.pilot.service;

import in.beze.pilot.domain.TaskHistory;
import in.beze.pilot.repository.TaskHistoryRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.TaskHistory}.
 */
@Service
@Transactional
public class TaskHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskHistoryService.class);

    private final TaskHistoryRepository taskHistoryRepository;

    public TaskHistoryService(TaskHistoryRepository taskHistoryRepository) {
        this.taskHistoryRepository = taskHistoryRepository;
    }

    /**
     * Save a taskHistory.
     *
     * @param taskHistory the entity to save.
     * @return the persisted entity.
     */
    public TaskHistory save(TaskHistory taskHistory) {
        LOG.debug("Request to save TaskHistory : {}", taskHistory);
        return taskHistoryRepository.save(taskHistory);
    }

    /**
     * Update a taskHistory.
     *
     * @param taskHistory the entity to save.
     * @return the persisted entity.
     */
    public TaskHistory update(TaskHistory taskHistory) {
        LOG.debug("Request to update TaskHistory : {}", taskHistory);
        return taskHistoryRepository.save(taskHistory);
    }

    /**
     * Partially update a taskHistory.
     *
     * @param taskHistory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TaskHistory> partialUpdate(TaskHistory taskHistory) {
        LOG.debug("Request to partially update TaskHistory : {}", taskHistory);

        return taskHistoryRepository
            .findById(taskHistory.getId())
            .map(existingTaskHistory -> {
                if (taskHistory.getType() != null) {
                    existingTaskHistory.setType(taskHistory.getType());
                }

                return existingTaskHistory;
            })
            .map(taskHistoryRepository::save);
    }

    /**
     * Get all the taskHistories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TaskHistory> findAllWithEagerRelationships(Pageable pageable) {
        return taskHistoryRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one taskHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TaskHistory> findOne(Long id) {
        LOG.debug("Request to get TaskHistory : {}", id);
        return taskHistoryRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the taskHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TaskHistory : {}", id);
        taskHistoryRepository.deleteById(id);
    }
}
