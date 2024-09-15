package in.beze.pilot.web.rest;

import in.beze.pilot.domain.TaskHistory;
import in.beze.pilot.repository.TaskHistoryRepository;
import in.beze.pilot.service.TaskHistoryQueryService;
import in.beze.pilot.service.TaskHistoryService;
import in.beze.pilot.service.criteria.TaskHistoryCriteria;
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
 * REST controller for managing {@link in.beze.pilot.domain.TaskHistory}.
 */
@RestController
@RequestMapping("/api/task-histories")
public class TaskHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(TaskHistoryResource.class);

    private static final String ENTITY_NAME = "taskHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskHistoryService taskHistoryService;

    private final TaskHistoryRepository taskHistoryRepository;

    private final TaskHistoryQueryService taskHistoryQueryService;

    public TaskHistoryResource(
        TaskHistoryService taskHistoryService,
        TaskHistoryRepository taskHistoryRepository,
        TaskHistoryQueryService taskHistoryQueryService
    ) {
        this.taskHistoryService = taskHistoryService;
        this.taskHistoryRepository = taskHistoryRepository;
        this.taskHistoryQueryService = taskHistoryQueryService;
    }

    /**
     * {@code POST  /task-histories} : Create a new taskHistory.
     *
     * @param taskHistory the taskHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskHistory, or with status {@code 400 (Bad Request)} if the taskHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TaskHistory> createTaskHistory(@RequestBody TaskHistory taskHistory) throws URISyntaxException {
        LOG.debug("REST request to save TaskHistory : {}", taskHistory);
        if (taskHistory.getId() != null) {
            throw new BadRequestAlertException("A new taskHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        taskHistory = taskHistoryService.save(taskHistory);
        return ResponseEntity.created(new URI("/api/task-histories/" + taskHistory.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, taskHistory.getId().toString()))
            .body(taskHistory);
    }

    /**
     * {@code PUT  /task-histories/:id} : Updates an existing taskHistory.
     *
     * @param id the id of the taskHistory to save.
     * @param taskHistory the taskHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskHistory,
     * or with status {@code 400 (Bad Request)} if the taskHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskHistory> updateTaskHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TaskHistory taskHistory
    ) throws URISyntaxException {
        LOG.debug("REST request to update TaskHistory : {}, {}", id, taskHistory);
        if (taskHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        taskHistory = taskHistoryService.update(taskHistory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskHistory.getId().toString()))
            .body(taskHistory);
    }

    /**
     * {@code PATCH  /task-histories/:id} : Partial updates given fields of an existing taskHistory, field will ignore if it is null
     *
     * @param id the id of the taskHistory to save.
     * @param taskHistory the taskHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskHistory,
     * or with status {@code 400 (Bad Request)} if the taskHistory is not valid,
     * or with status {@code 404 (Not Found)} if the taskHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskHistory> partialUpdateTaskHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TaskHistory taskHistory
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TaskHistory partially : {}, {}", id, taskHistory);
        if (taskHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskHistory> result = taskHistoryService.partialUpdate(taskHistory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /task-histories} : get all the taskHistories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TaskHistory>> getAllTaskHistories(
        TaskHistoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TaskHistories by criteria: {}", criteria);

        Page<TaskHistory> page = taskHistoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /task-histories/count} : count all the taskHistories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTaskHistories(TaskHistoryCriteria criteria) {
        LOG.debug("REST request to count TaskHistories by criteria: {}", criteria);
        return ResponseEntity.ok().body(taskHistoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /task-histories/:id} : get the "id" taskHistory.
     *
     * @param id the id of the taskHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskHistory> getTaskHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TaskHistory : {}", id);
        Optional<TaskHistory> taskHistory = taskHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskHistory);
    }

    /**
     * {@code DELETE  /task-histories/:id} : delete the "id" taskHistory.
     *
     * @param id the id of the taskHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TaskHistory : {}", id);
        taskHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
