package in.beze.pilot.service;

import in.beze.pilot.domain.TaskAttachment;
import in.beze.pilot.repository.TaskAttachmentRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.TaskAttachment}.
 */
@Service
@Transactional
public class TaskAttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskAttachmentService.class);

    private final TaskAttachmentRepository taskAttachmentRepository;

    public TaskAttachmentService(TaskAttachmentRepository taskAttachmentRepository) {
        this.taskAttachmentRepository = taskAttachmentRepository;
    }

    /**
     * Save a taskAttachment.
     *
     * @param taskAttachment the entity to save.
     * @return the persisted entity.
     */
    public TaskAttachment save(TaskAttachment taskAttachment) {
        LOG.debug("Request to save TaskAttachment : {}", taskAttachment);
        return taskAttachmentRepository.save(taskAttachment);
    }

    /**
     * Update a taskAttachment.
     *
     * @param taskAttachment the entity to save.
     * @return the persisted entity.
     */
    public TaskAttachment update(TaskAttachment taskAttachment) {
        LOG.debug("Request to update TaskAttachment : {}", taskAttachment);
        return taskAttachmentRepository.save(taskAttachment);
    }

    /**
     * Partially update a taskAttachment.
     *
     * @param taskAttachment the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TaskAttachment> partialUpdate(TaskAttachment taskAttachment) {
        LOG.debug("Request to partially update TaskAttachment : {}", taskAttachment);

        return taskAttachmentRepository.findById(taskAttachment.getId()).map(taskAttachmentRepository::save);
    }

    /**
     * Get all the taskAttachments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TaskAttachment> findAllWithEagerRelationships(Pageable pageable) {
        return taskAttachmentRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one taskAttachment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TaskAttachment> findOne(Long id) {
        LOG.debug("Request to get TaskAttachment : {}", id);
        return taskAttachmentRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the taskAttachment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TaskAttachment : {}", id);
        taskAttachmentRepository.deleteById(id);
    }
}
