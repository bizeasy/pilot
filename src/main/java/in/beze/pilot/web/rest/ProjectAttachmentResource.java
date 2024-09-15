package in.beze.pilot.web.rest;

import in.beze.pilot.domain.ProjectAttachment;
import in.beze.pilot.repository.ProjectAttachmentRepository;
import in.beze.pilot.service.ProjectAttachmentQueryService;
import in.beze.pilot.service.ProjectAttachmentService;
import in.beze.pilot.service.criteria.ProjectAttachmentCriteria;
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
 * REST controller for managing {@link in.beze.pilot.domain.ProjectAttachment}.
 */
@RestController
@RequestMapping("/api/project-attachments")
public class ProjectAttachmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectAttachmentResource.class);

    private static final String ENTITY_NAME = "projectAttachment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectAttachmentService projectAttachmentService;

    private final ProjectAttachmentRepository projectAttachmentRepository;

    private final ProjectAttachmentQueryService projectAttachmentQueryService;

    public ProjectAttachmentResource(
        ProjectAttachmentService projectAttachmentService,
        ProjectAttachmentRepository projectAttachmentRepository,
        ProjectAttachmentQueryService projectAttachmentQueryService
    ) {
        this.projectAttachmentService = projectAttachmentService;
        this.projectAttachmentRepository = projectAttachmentRepository;
        this.projectAttachmentQueryService = projectAttachmentQueryService;
    }

    /**
     * {@code POST  /project-attachments} : Create a new projectAttachment.
     *
     * @param projectAttachment the projectAttachment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectAttachment, or with status {@code 400 (Bad Request)} if the projectAttachment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProjectAttachment> createProjectAttachment(@RequestBody ProjectAttachment projectAttachment)
        throws URISyntaxException {
        LOG.debug("REST request to save ProjectAttachment : {}", projectAttachment);
        if (projectAttachment.getId() != null) {
            throw new BadRequestAlertException("A new projectAttachment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        projectAttachment = projectAttachmentService.save(projectAttachment);
        return ResponseEntity.created(new URI("/api/project-attachments/" + projectAttachment.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, projectAttachment.getId().toString()))
            .body(projectAttachment);
    }

    /**
     * {@code PUT  /project-attachments/:id} : Updates an existing projectAttachment.
     *
     * @param id the id of the projectAttachment to save.
     * @param projectAttachment the projectAttachment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectAttachment,
     * or with status {@code 400 (Bad Request)} if the projectAttachment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectAttachment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectAttachment> updateProjectAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProjectAttachment projectAttachment
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProjectAttachment : {}, {}", id, projectAttachment);
        if (projectAttachment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectAttachment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        projectAttachment = projectAttachmentService.update(projectAttachment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectAttachment.getId().toString()))
            .body(projectAttachment);
    }

    /**
     * {@code PATCH  /project-attachments/:id} : Partial updates given fields of an existing projectAttachment, field will ignore if it is null
     *
     * @param id the id of the projectAttachment to save.
     * @param projectAttachment the projectAttachment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectAttachment,
     * or with status {@code 400 (Bad Request)} if the projectAttachment is not valid,
     * or with status {@code 404 (Not Found)} if the projectAttachment is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectAttachment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectAttachment> partialUpdateProjectAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProjectAttachment projectAttachment
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProjectAttachment partially : {}, {}", id, projectAttachment);
        if (projectAttachment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectAttachment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectAttachment> result = projectAttachmentService.partialUpdate(projectAttachment);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectAttachment.getId().toString())
        );
    }

    /**
     * {@code GET  /project-attachments} : get all the projectAttachments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectAttachments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProjectAttachment>> getAllProjectAttachments(
        ProjectAttachmentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ProjectAttachments by criteria: {}", criteria);

        Page<ProjectAttachment> page = projectAttachmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /project-attachments/count} : count all the projectAttachments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countProjectAttachments(ProjectAttachmentCriteria criteria) {
        LOG.debug("REST request to count ProjectAttachments by criteria: {}", criteria);
        return ResponseEntity.ok().body(projectAttachmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /project-attachments/:id} : get the "id" projectAttachment.
     *
     * @param id the id of the projectAttachment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectAttachment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectAttachment> getProjectAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProjectAttachment : {}", id);
        Optional<ProjectAttachment> projectAttachment = projectAttachmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectAttachment);
    }

    /**
     * {@code DELETE  /project-attachments/:id} : delete the "id" projectAttachment.
     *
     * @param id the id of the projectAttachment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProjectAttachment : {}", id);
        projectAttachmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
