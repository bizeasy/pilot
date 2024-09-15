package in.beze.pilot.service;

import in.beze.pilot.domain.Task;
import in.beze.pilot.repository.TaskRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.Task}.
 */
@Service
@Transactional
public class TaskService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Save a task.
     *
     * @param task the entity to save.
     * @return the persisted entity.
     */
    public Task save(Task task) {
        LOG.debug("Request to save Task : {}", task);
        return taskRepository.save(task);
    }

    /**
     * Update a task.
     *
     * @param task the entity to save.
     * @return the persisted entity.
     */
    public Task update(Task task) {
        LOG.debug("Request to update Task : {}", task);
        return taskRepository.save(task);
    }

    /**
     * Partially update a task.
     *
     * @param task the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Task> partialUpdate(Task task) {
        LOG.debug("Request to partially update Task : {}", task);

        return taskRepository
            .findById(task.getId())
            .map(existingTask -> {
                if (task.getTitle() != null) {
                    existingTask.setTitle(task.getTitle());
                }
                if (task.getDescription() != null) {
                    existingTask.setDescription(task.getDescription());
                }
                if (task.getPriority() != null) {
                    existingTask.setPriority(task.getPriority());
                }
                if (task.getDueDate() != null) {
                    existingTask.setDueDate(task.getDueDate());
                }
                if (task.getInfo() != null) {
                    existingTask.setInfo(task.getInfo());
                }
                if (task.getStoryPoints() != null) {
                    existingTask.setStoryPoints(task.getStoryPoints());
                }
                if (task.getStartTime() != null) {
                    existingTask.setStartTime(task.getStartTime());
                }
                if (task.getPauseTime() != null) {
                    existingTask.setPauseTime(task.getPauseTime());
                }
                if (task.getEndTime() != null) {
                    existingTask.setEndTime(task.getEndTime());
                }
                if (task.getTotalDuration() != null) {
                    existingTask.setTotalDuration(task.getTotalDuration());
                }
                if (task.getSequenceNo() != null) {
                    existingTask.setSequenceNo(task.getSequenceNo());
                }

                return existingTask;
            })
            .map(taskRepository::save);
    }

    /**
     * Get all the tasks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Task> findAllWithEagerRelationships(Pageable pageable) {
        return taskRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one task by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Task> findOne(Long id) {
        LOG.debug("Request to get Task : {}", id);
        return taskRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the task by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Task : {}", id);
        taskRepository.deleteById(id);
    }
}
