package in.beze.pilot.service;

import in.beze.pilot.domain.TaskLog;
import in.beze.pilot.repository.TaskLogRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.TaskLog}.
 */
@Service
@Transactional
public class TaskLogService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskLogService.class);

    private final TaskLogRepository taskLogRepository;

    public TaskLogService(TaskLogRepository taskLogRepository) {
        this.taskLogRepository = taskLogRepository;
    }

    /**
     * Save a taskLog.
     *
     * @param taskLog the entity to save.
     * @return the persisted entity.
     */
    public TaskLog save(TaskLog taskLog) {
        LOG.debug("Request to save TaskLog : {}", taskLog);
        return taskLogRepository.save(taskLog);
    }

    /**
     * Update a taskLog.
     *
     * @param taskLog the entity to save.
     * @return the persisted entity.
     */
    public TaskLog update(TaskLog taskLog) {
        LOG.debug("Request to update TaskLog : {}", taskLog);
        return taskLogRepository.save(taskLog);
    }

    /**
     * Partially update a taskLog.
     *
     * @param taskLog the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TaskLog> partialUpdate(TaskLog taskLog) {
        LOG.debug("Request to partially update TaskLog : {}", taskLog);

        return taskLogRepository
            .findById(taskLog.getId())
            .map(existingTaskLog -> {
                if (taskLog.getComments() != null) {
                    existingTaskLog.setComments(taskLog.getComments());
                }
                if (taskLog.getFromTime() != null) {
                    existingTaskLog.setFromTime(taskLog.getFromTime());
                }
                if (taskLog.getToTime() != null) {
                    existingTaskLog.setToTime(taskLog.getToTime());
                }

                return existingTaskLog;
            })
            .map(taskLogRepository::save);
    }

    /**
     * Get one taskLog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TaskLog> findOne(Long id) {
        LOG.debug("Request to get TaskLog : {}", id);
        return taskLogRepository.findById(id);
    }

    /**
     * Delete the taskLog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TaskLog : {}", id);
        taskLogRepository.deleteById(id);
    }
}
