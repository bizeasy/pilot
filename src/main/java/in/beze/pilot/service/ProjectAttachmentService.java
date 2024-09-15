package in.beze.pilot.service;

import in.beze.pilot.domain.ProjectAttachment;
import in.beze.pilot.repository.ProjectAttachmentRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.ProjectAttachment}.
 */
@Service
@Transactional
public class ProjectAttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectAttachmentService.class);

    private final ProjectAttachmentRepository projectAttachmentRepository;

    public ProjectAttachmentService(ProjectAttachmentRepository projectAttachmentRepository) {
        this.projectAttachmentRepository = projectAttachmentRepository;
    }

    /**
     * Save a projectAttachment.
     *
     * @param projectAttachment the entity to save.
     * @return the persisted entity.
     */
    public ProjectAttachment save(ProjectAttachment projectAttachment) {
        LOG.debug("Request to save ProjectAttachment : {}", projectAttachment);
        return projectAttachmentRepository.save(projectAttachment);
    }

    /**
     * Update a projectAttachment.
     *
     * @param projectAttachment the entity to save.
     * @return the persisted entity.
     */
    public ProjectAttachment update(ProjectAttachment projectAttachment) {
        LOG.debug("Request to update ProjectAttachment : {}", projectAttachment);
        return projectAttachmentRepository.save(projectAttachment);
    }

    /**
     * Partially update a projectAttachment.
     *
     * @param projectAttachment the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProjectAttachment> partialUpdate(ProjectAttachment projectAttachment) {
        LOG.debug("Request to partially update ProjectAttachment : {}", projectAttachment);

        return projectAttachmentRepository.findById(projectAttachment.getId()).map(projectAttachmentRepository::save);
    }

    /**
     * Get all the projectAttachments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProjectAttachment> findAllWithEagerRelationships(Pageable pageable) {
        return projectAttachmentRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one projectAttachment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProjectAttachment> findOne(Long id) {
        LOG.debug("Request to get ProjectAttachment : {}", id);
        return projectAttachmentRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the projectAttachment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ProjectAttachment : {}", id);
        projectAttachmentRepository.deleteById(id);
    }
}
