package in.beze.pilot.service;

import in.beze.pilot.domain.Attachment;
import in.beze.pilot.repository.AttachmentRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.Attachment}.
 */
@Service
@Transactional
public class AttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(AttachmentService.class);

    private final AttachmentRepository attachmentRepository;

    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    /**
     * Save a attachment.
     *
     * @param attachment the entity to save.
     * @return the persisted entity.
     */
    public Attachment save(Attachment attachment) {
        LOG.debug("Request to save Attachment : {}", attachment);
        return attachmentRepository.save(attachment);
    }

    /**
     * Update a attachment.
     *
     * @param attachment the entity to save.
     * @return the persisted entity.
     */
    public Attachment update(Attachment attachment) {
        LOG.debug("Request to update Attachment : {}", attachment);
        return attachmentRepository.save(attachment);
    }

    /**
     * Partially update a attachment.
     *
     * @param attachment the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Attachment> partialUpdate(Attachment attachment) {
        LOG.debug("Request to partially update Attachment : {}", attachment);

        return attachmentRepository
            .findById(attachment.getId())
            .map(existingAttachment -> {
                if (attachment.getName() != null) {
                    existingAttachment.setName(attachment.getName());
                }
                if (attachment.getFileAttachment() != null) {
                    existingAttachment.setFileAttachment(attachment.getFileAttachment());
                }
                if (attachment.getFileAttachmentContentType() != null) {
                    existingAttachment.setFileAttachmentContentType(attachment.getFileAttachmentContentType());
                }
                if (attachment.getAttachmentUrl() != null) {
                    existingAttachment.setAttachmentUrl(attachment.getAttachmentUrl());
                }
                if (attachment.getMimeType() != null) {
                    existingAttachment.setMimeType(attachment.getMimeType());
                }

                return existingAttachment;
            })
            .map(attachmentRepository::save);
    }

    /**
     * Get all the attachments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Attachment> findAll(Pageable pageable) {
        LOG.debug("Request to get all Attachments");
        return attachmentRepository.findAll(pageable);
    }

    /**
     * Get one attachment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Attachment> findOne(Long id) {
        LOG.debug("Request to get Attachment : {}", id);
        return attachmentRepository.findById(id);
    }

    /**
     * Delete the attachment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Attachment : {}", id);
        attachmentRepository.deleteById(id);
    }
}
