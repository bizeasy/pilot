package in.beze.pilot.web.rest;

import in.beze.pilot.domain.SprintAttachment;
import in.beze.pilot.repository.SprintAttachmentRepository;
import in.beze.pilot.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link in.beze.pilot.domain.SprintAttachment}.
 */
@RestController
@RequestMapping("/api/sprint-attachments")
@Transactional
public class SprintAttachmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(SprintAttachmentResource.class);

    private static final String ENTITY_NAME = "sprintAttachment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SprintAttachmentRepository sprintAttachmentRepository;

    public SprintAttachmentResource(SprintAttachmentRepository sprintAttachmentRepository) {
        this.sprintAttachmentRepository = sprintAttachmentRepository;
    }

    /**
     * {@code POST  /sprint-attachments} : Create a new sprintAttachment.
     *
     * @param sprintAttachment the sprintAttachment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sprintAttachment, or with status {@code 400 (Bad Request)} if the sprintAttachment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SprintAttachment> createSprintAttachment(@RequestBody SprintAttachment sprintAttachment)
        throws URISyntaxException {
        LOG.debug("REST request to save SprintAttachment : {}", sprintAttachment);
        if (sprintAttachment.getId() != null) {
            throw new BadRequestAlertException("A new sprintAttachment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sprintAttachment = sprintAttachmentRepository.save(sprintAttachment);
        return ResponseEntity.created(new URI("/api/sprint-attachments/" + sprintAttachment.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, sprintAttachment.getId().toString()))
            .body(sprintAttachment);
    }

    /**
     * {@code PUT  /sprint-attachments/:id} : Updates an existing sprintAttachment.
     *
     * @param id the id of the sprintAttachment to save.
     * @param sprintAttachment the sprintAttachment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sprintAttachment,
     * or with status {@code 400 (Bad Request)} if the sprintAttachment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sprintAttachment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SprintAttachment> updateSprintAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SprintAttachment sprintAttachment
    ) throws URISyntaxException {
        LOG.debug("REST request to update SprintAttachment : {}, {}", id, sprintAttachment);
        if (sprintAttachment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sprintAttachment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sprintAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sprintAttachment = sprintAttachmentRepository.save(sprintAttachment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sprintAttachment.getId().toString()))
            .body(sprintAttachment);
    }

    /**
     * {@code PATCH  /sprint-attachments/:id} : Partial updates given fields of an existing sprintAttachment, field will ignore if it is null
     *
     * @param id the id of the sprintAttachment to save.
     * @param sprintAttachment the sprintAttachment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sprintAttachment,
     * or with status {@code 400 (Bad Request)} if the sprintAttachment is not valid,
     * or with status {@code 404 (Not Found)} if the sprintAttachment is not found,
     * or with status {@code 500 (Internal Server Error)} if the sprintAttachment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SprintAttachment> partialUpdateSprintAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SprintAttachment sprintAttachment
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SprintAttachment partially : {}, {}", id, sprintAttachment);
        if (sprintAttachment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sprintAttachment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sprintAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SprintAttachment> result = sprintAttachmentRepository
            .findById(sprintAttachment.getId())
            .map(sprintAttachmentRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sprintAttachment.getId().toString())
        );
    }

    /**
     * {@code GET  /sprint-attachments} : get all the sprintAttachments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sprintAttachments in body.
     */
    @GetMapping("")
    public List<SprintAttachment> getAllSprintAttachments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SprintAttachments");
        if (eagerload) {
            return sprintAttachmentRepository.findAllWithEagerRelationships();
        } else {
            return sprintAttachmentRepository.findAll();
        }
    }

    /**
     * {@code GET  /sprint-attachments/:id} : get the "id" sprintAttachment.
     *
     * @param id the id of the sprintAttachment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sprintAttachment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SprintAttachment> getSprintAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SprintAttachment : {}", id);
        Optional<SprintAttachment> sprintAttachment = sprintAttachmentRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(sprintAttachment);
    }

    /**
     * {@code DELETE  /sprint-attachments/:id} : delete the "id" sprintAttachment.
     *
     * @param id the id of the sprintAttachment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSprintAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SprintAttachment : {}", id);
        sprintAttachmentRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
