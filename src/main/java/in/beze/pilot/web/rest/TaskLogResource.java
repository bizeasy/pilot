package in.beze.pilot.web.rest;

import in.beze.pilot.domain.TaskLog;
import in.beze.pilot.repository.TaskLogRepository;
import in.beze.pilot.service.TaskLogQueryService;
import in.beze.pilot.service.TaskLogService;
import in.beze.pilot.service.criteria.TaskLogCriteria;
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
 * REST controller for managing {@link in.beze.pilot.domain.TaskLog}.
 */
@RestController
@RequestMapping("/api/task-logs")
public class TaskLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(TaskLogResource.class);

    private static final String ENTITY_NAME = "taskLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskLogService taskLogService;

    private final TaskLogRepository taskLogRepository;

    private final TaskLogQueryService taskLogQueryService;

    public TaskLogResource(TaskLogService taskLogService, TaskLogRepository taskLogRepository, TaskLogQueryService taskLogQueryService) {
        this.taskLogService = taskLogService;
        this.taskLogRepository = taskLogRepository;
        this.taskLogQueryService = taskLogQueryService;
    }

    /**
     * {@code POST  /task-logs} : Create a new taskLog.
     *
     * @param taskLog the taskLog to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskLog, or with status {@code 400 (Bad Request)} if the taskLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TaskLog> createTaskLog(@RequestBody TaskLog taskLog) throws URISyntaxException {
        LOG.debug("REST request to save TaskLog : {}", taskLog);
        if (taskLog.getId() != null) {
            throw new BadRequestAlertException("A new taskLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        taskLog = taskLogService.save(taskLog);
        return ResponseEntity.created(new URI("/api/task-logs/" + taskLog.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, taskLog.getId().toString()))
            .body(taskLog);
    }

    /**
     * {@code PUT  /task-logs/:id} : Updates an existing taskLog.
     *
     * @param id the id of the taskLog to save.
     * @param taskLog the taskLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskLog,
     * or with status {@code 400 (Bad Request)} if the taskLog is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskLog> updateTaskLog(@PathVariable(value = "id", required = false) final Long id, @RequestBody TaskLog taskLog)
        throws URISyntaxException {
        LOG.debug("REST request to update TaskLog : {}, {}", id, taskLog);
        if (taskLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskLog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        taskLog = taskLogService.update(taskLog);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskLog.getId().toString()))
            .body(taskLog);
    }

    /**
     * {@code PATCH  /task-logs/:id} : Partial updates given fields of an existing taskLog, field will ignore if it is null
     *
     * @param id the id of the taskLog to save.
     * @param taskLog the taskLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskLog,
     * or with status {@code 400 (Bad Request)} if the taskLog is not valid,
     * or with status {@code 404 (Not Found)} if the taskLog is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskLog> partialUpdateTaskLog(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TaskLog taskLog
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TaskLog partially : {}, {}", id, taskLog);
        if (taskLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskLog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskLog> result = taskLogService.partialUpdate(taskLog);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskLog.getId().toString())
        );
    }

    /**
     * {@code GET  /task-logs} : get all the taskLogs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskLogs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TaskLog>> getAllTaskLogs(
        TaskLogCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TaskLogs by criteria: {}", criteria);

        Page<TaskLog> page = taskLogQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /task-logs/count} : count all the taskLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTaskLogs(TaskLogCriteria criteria) {
        LOG.debug("REST request to count TaskLogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(taskLogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /task-logs/:id} : get the "id" taskLog.
     *
     * @param id the id of the taskLog to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskLog, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskLog> getTaskLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TaskLog : {}", id);
        Optional<TaskLog> taskLog = taskLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskLog);
    }

    /**
     * {@code DELETE  /task-logs/:id} : delete the "id" taskLog.
     *
     * @param id the id of the taskLog to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TaskLog : {}", id);
        taskLogService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
