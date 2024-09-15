package in.beze.pilot.web.rest;

import in.beze.pilot.domain.TaskAttachment;
import in.beze.pilot.repository.TaskAttachmentRepository;
import in.beze.pilot.service.TaskAttachmentQueryService;
import in.beze.pilot.service.TaskAttachmentService;
import in.beze.pilot.service.criteria.TaskAttachmentCriteria;
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
 * REST controller for managing {@link in.beze.pilot.domain.TaskAttachment}.
 */
@RestController
@RequestMapping("/api/task-attachments")
public class TaskAttachmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(TaskAttachmentResource.class);

    private static final String ENTITY_NAME = "taskAttachment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskAttachmentService taskAttachmentService;

    private final TaskAttachmentRepository taskAttachmentRepository;

    private final TaskAttachmentQueryService taskAttachmentQueryService;

    public TaskAttachmentResource(
        TaskAttachmentService taskAttachmentService,
        TaskAttachmentRepository taskAttachmentRepository,
        TaskAttachmentQueryService taskAttachmentQueryService
    ) {
        this.taskAttachmentService = taskAttachmentService;
        this.taskAttachmentRepository = taskAttachmentRepository;
        this.taskAttachmentQueryService = taskAttachmentQueryService;
    }

    /**
     * {@code POST  /task-attachments} : Create a new taskAttachment.
     *
     * @param taskAttachment the taskAttachment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskAttachment, or with status {@code 400 (Bad Request)} if the taskAttachment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TaskAttachment> createTaskAttachment(@RequestBody TaskAttachment taskAttachment) throws URISyntaxException {
        LOG.debug("REST request to save TaskAttachment : {}", taskAttachment);
        if (taskAttachment.getId() != null) {
            throw new BadRequestAlertException("A new taskAttachment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        taskAttachment = taskAttachmentService.save(taskAttachment);
        return ResponseEntity.created(new URI("/api/task-attachments/" + taskAttachment.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, taskAttachment.getId().toString()))
            .body(taskAttachment);
    }

    /**
     * {@code PUT  /task-attachments/:id} : Updates an existing taskAttachment.
     *
     * @param id the id of the taskAttachment to save.
     * @param taskAttachment the taskAttachment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskAttachment,
     * or with status {@code 400 (Bad Request)} if the taskAttachment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskAttachment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskAttachment> updateTaskAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TaskAttachment taskAttachment
    ) throws URISyntaxException {
        LOG.debug("REST request to update TaskAttachment : {}, {}", id, taskAttachment);
        if (taskAttachment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskAttachment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        taskAttachment = taskAttachmentService.update(taskAttachment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskAttachment.getId().toString()))
            .body(taskAttachment);
    }

    /**
     * {@code PATCH  /task-attachments/:id} : Partial updates given fields of an existing taskAttachment, field will ignore if it is null
     *
     * @param id the id of the taskAttachment to save.
     * @param taskAttachment the taskAttachment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskAttachment,
     * or with status {@code 400 (Bad Request)} if the taskAttachment is not valid,
     * or with status {@code 404 (Not Found)} if the taskAttachment is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskAttachment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskAttachment> partialUpdateTaskAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TaskAttachment taskAttachment
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TaskAttachment partially : {}, {}", id, taskAttachment);
        if (taskAttachment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskAttachment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskAttachment> result = taskAttachmentService.partialUpdate(taskAttachment);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskAttachment.getId().toString())
        );
    }

    /**
     * {@code GET  /task-attachments} : get all the taskAttachments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskAttachments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TaskAttachment>> getAllTaskAttachments(
        TaskAttachmentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TaskAttachments by criteria: {}", criteria);

        Page<TaskAttachment> page = taskAttachmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /task-attachments/count} : count all the taskAttachments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTaskAttachments(TaskAttachmentCriteria criteria) {
        LOG.debug("REST request to count TaskAttachments by criteria: {}", criteria);
        return ResponseEntity.ok().body(taskAttachmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /task-attachments/:id} : get the "id" taskAttachment.
     *
     * @param id the id of the taskAttachment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskAttachment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskAttachment> getTaskAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TaskAttachment : {}", id);
        Optional<TaskAttachment> taskAttachment = taskAttachmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskAttachment);
    }

    /**
     * {@code DELETE  /task-attachments/:id} : delete the "id" taskAttachment.
     *
     * @param id the id of the taskAttachment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TaskAttachment : {}", id);
        taskAttachmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
