package in.beze.pilot.service;

import com.google.zxing.WriterException;
import in.beze.pilot.domain.Attachment;
import in.beze.pilot.domain.Sprint;
import in.beze.pilot.repository.AttachmentRepository;
import in.beze.pilot.repository.SprintRepository;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.Sprint}.
 */
@Service
@Transactional
public class SprintService {

    private static final Logger LOG = LoggerFactory.getLogger(SprintService.class);

    private final SprintRepository sprintRepository;

    private final QrCodeGenerator qrCodeGenerator;

    private final AttachmentRepository attachmentRepository;

    public SprintService(SprintRepository sprintRepository, QrCodeGenerator qrCodeGenerator, AttachmentRepository attachmentRepository) {
        this.sprintRepository = sprintRepository;
        this.qrCodeGenerator = qrCodeGenerator;
        this.attachmentRepository = attachmentRepository;
    }

    /**
     * Save a sprint.
     *
     * @param sprint the entity to save.
     * @return the persisted entity.
     */
    public Sprint save(Sprint sprint) {
        LOG.debug("Request to save Sprint : {}", sprint);
        sprint = sprintRepository.save(sprint);
        Attachment attachment = new Attachment();

        // Generate QR Code Image
        byte[] qrCode = new byte[0];
        try {
            qrCode = qrCodeGenerator.generateQRCode(sprint.getId().toString(), 200, 200);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (WriterException e1) {
            throw new RuntimeException(e1);
        }
        LOG.debug("qrCode=================================================" + qrCode);
        Attachment at = new Attachment();
        at.setFileAttachment(qrCode);
        at.setFileAttachmentContentType("image/png");
        attachmentRepository.save(at);

        // Generate Bar Code

        byte[] barCode = new byte[0];
        try {
            barCode = qrCodeGenerator.generateBarCode(sprint.getId().toString(), 100, 200);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (WriterException e1) {
            throw new RuntimeException(e1);
        }
        LOG.debug("barCode=================================================" + barCode);
        Attachment at1 = new Attachment();
        at1.setFileAttachment(barCode);
        at1.setFileAttachmentContentType("image/png");
        attachmentRepository.save(at1);

        return sprint;
    }

    /**
     * Update a sprint.
     *
     * @param sprint the entity to save.
     * @return the persisted entity.
     */
    public Sprint update(Sprint sprint) {
        LOG.debug("Request to update Sprint : {}", sprint);
        return sprintRepository.save(sprint);
    }

    /**
     * Partially update a sprint.
     *
     * @param sprint the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Sprint> partialUpdate(Sprint sprint) {
        LOG.debug("Request to partially update Sprint : {}", sprint);

        return sprintRepository
            .findById(sprint.getId())
            .map(existingSprint -> {
                if (sprint.getName() != null) {
                    existingSprint.setName(sprint.getName());
                }
                if (sprint.getStartDate() != null) {
                    existingSprint.setStartDate(sprint.getStartDate());
                }
                if (sprint.getEndDate() != null) {
                    existingSprint.setEndDate(sprint.getEndDate());
                }
                if (sprint.getGoal() != null) {
                    existingSprint.setGoal(sprint.getGoal());
                }
                if (sprint.getTotalPoints() != null) {
                    existingSprint.setTotalPoints(sprint.getTotalPoints());
                }

                return existingSprint;
            })
            .map(sprintRepository::save);
    }

    /**
     * Get all the sprints with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Sprint> findAllWithEagerRelationships(Pageable pageable) {
        return sprintRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one sprint by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Sprint> findOne(Long id) {
        LOG.debug("Request to get Sprint : {}", id);
        return sprintRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the sprint by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Sprint : {}", id);
        sprintRepository.deleteById(id);
    }
}
