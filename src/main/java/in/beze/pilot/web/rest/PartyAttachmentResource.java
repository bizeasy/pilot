package in.beze.pilot.web.rest;

import in.beze.pilot.domain.PartyAttachment;
import in.beze.pilot.repository.PartyAttachmentRepository;
import in.beze.pilot.service.PartyAttachmentQueryService;
import in.beze.pilot.service.PartyAttachmentService;
import in.beze.pilot.service.criteria.PartyAttachmentCriteria;
import in.beze.pilot.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link in.beze.pilot.domain.PartyAttachment}.
 */
@RestController
@RequestMapping("/api/party-attachments")
public class PartyAttachmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(PartyAttachmentResource.class);

    private static final String ENTITY_NAME = "partyAttachment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PartyAttachmentService partyAttachmentService;

    private final PartyAttachmentRepository partyAttachmentRepository;

    private final PartyAttachmentQueryService partyAttachmentQueryService;

    public PartyAttachmentResource(
        PartyAttachmentService partyAttachmentService,
        PartyAttachmentRepository partyAttachmentRepository,
        PartyAttachmentQueryService partyAttachmentQueryService
    ) {
        this.partyAttachmentService = partyAttachmentService;
        this.partyAttachmentRepository = partyAttachmentRepository;
        this.partyAttachmentQueryService = partyAttachmentQueryService;
    }

    /**
     * {@code POST  /party-attachments} : Create a new partyAttachment.
     *
     * @param partyAttachment the partyAttachment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new partyAttachment, or with status {@code 400 (Bad Request)} if the partyAttachment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PartyAttachment> createPartyAttachment(@RequestBody PartyAttachment partyAttachment) throws URISyntaxException {
        LOG.debug("REST request to save PartyAttachment : {}", partyAttachment);
        if (partyAttachment.getId() != null) {
            throw new BadRequestAlertException("A new partyAttachment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        partyAttachment = partyAttachmentService.save(partyAttachment);
        return ResponseEntity.created(new URI("/api/party-attachments/" + partyAttachment.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, partyAttachment.getId().toString()))
            .body(partyAttachment);
    }

    /**
     * {@code PUT  /party-attachments/:id} : Updates an existing partyAttachment.
     *
     * @param id the id of the partyAttachment to save.
     * @param partyAttachment the partyAttachment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partyAttachment,
     * or with status {@code 400 (Bad Request)} if the partyAttachment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the partyAttachment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PartyAttachment> updatePartyAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PartyAttachment partyAttachment
    ) throws URISyntaxException {
        LOG.debug("REST request to update PartyAttachment : {}, {}", id, partyAttachment);
        if (partyAttachment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partyAttachment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partyAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        partyAttachment = partyAttachmentService.update(partyAttachment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, partyAttachment.getId().toString()))
            .body(partyAttachment);
    }

    /**
     * {@code PATCH  /party-attachments/:id} : Partial updates given fields of an existing partyAttachment, field will ignore if it is null
     *
     * @param id the id of the partyAttachment to save.
     * @param partyAttachment the partyAttachment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partyAttachment,
     * or with status {@code 400 (Bad Request)} if the partyAttachment is not valid,
     * or with status {@code 404 (Not Found)} if the partyAttachment is not found,
     * or with status {@code 500 (Internal Server Error)} if the partyAttachment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PartyAttachment> partialUpdatePartyAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PartyAttachment partyAttachment
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PartyAttachment partially : {}, {}", id, partyAttachment);
        if (partyAttachment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partyAttachment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partyAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PartyAttachment> result = partyAttachmentService.partialUpdate(partyAttachment);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, partyAttachment.getId().toString())
        );
    }

    /**
     * {@code GET  /party-attachments} : get all the partyAttachments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of partyAttachments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PartyAttachment>> getAllPartyAttachments(
        PartyAttachmentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PartyAttachments by criteria: {}", criteria);

        Page<PartyAttachment> page = partyAttachmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /party-attachments/count} : count all the partyAttachments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPartyAttachments(PartyAttachmentCriteria criteria) {
        LOG.debug("REST request to count PartyAttachments by criteria: {}", criteria);
        return ResponseEntity.ok().body(partyAttachmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /party-attachments/:id} : get the "id" partyAttachment.
     *
     * @param id the id of the partyAttachment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the partyAttachment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PartyAttachment> getPartyAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PartyAttachment : {}", id);
        Optional<PartyAttachment> partyAttachment = partyAttachmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(partyAttachment);
    }

    /**
     * {@code DELETE  /party-attachments/:id} : delete the "id" partyAttachment.
     *
     * @param id the id of the partyAttachment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartyAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PartyAttachment : {}", id);
        partyAttachmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
