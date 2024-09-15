package in.beze.pilot.web.rest;

import in.beze.pilot.domain.SprintTask;
import in.beze.pilot.repository.SprintTaskRepository;
import in.beze.pilot.service.SprintTaskQueryService;
import in.beze.pilot.service.SprintTaskService;
import in.beze.pilot.service.criteria.SprintTaskCriteria;
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
 * REST controller for managing {@link in.beze.pilot.domain.SprintTask}.
 */
@RestController
@RequestMapping("/api/sprint-tasks")
public class SprintTaskResource {

    private static final Logger LOG = LoggerFactory.getLogger(SprintTaskResource.class);

    private static final String ENTITY_NAME = "sprintTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SprintTaskService sprintTaskService;

    private final SprintTaskRepository sprintTaskRepository;

    private final SprintTaskQueryService sprintTaskQueryService;

    public SprintTaskResource(
        SprintTaskService sprintTaskService,
        SprintTaskRepository sprintTaskRepository,
        SprintTaskQueryService sprintTaskQueryService
    ) {
        this.sprintTaskService = sprintTaskService;
        this.sprintTaskRepository = sprintTaskRepository;
        this.sprintTaskQueryService = sprintTaskQueryService;
    }

    /**
     * {@code POST  /sprint-tasks} : Create a new sprintTask.
     *
     * @param sprintTask the sprintTask to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sprintTask, or with status {@code 400 (Bad Request)} if the sprintTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SprintTask> createSprintTask(@RequestBody SprintTask sprintTask) throws URISyntaxException {
        LOG.debug("REST request to save SprintTask : {}", sprintTask);
        if (sprintTask.getId() != null) {
            throw new BadRequestAlertException("A new sprintTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sprintTask = sprintTaskService.save(sprintTask);
        return ResponseEntity.created(new URI("/api/sprint-tasks/" + sprintTask.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, sprintTask.getId().toString()))
            .body(sprintTask);
    }

    /**
     * {@code PUT  /sprint-tasks/:id} : Updates an existing sprintTask.
     *
     * @param id the id of the sprintTask to save.
     * @param sprintTask the sprintTask to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sprintTask,
     * or with status {@code 400 (Bad Request)} if the sprintTask is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sprintTask couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SprintTask> updateSprintTask(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SprintTask sprintTask
    ) throws URISyntaxException {
        LOG.debug("REST request to update SprintTask : {}, {}", id, sprintTask);
        if (sprintTask.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sprintTask.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sprintTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sprintTask = sprintTaskService.update(sprintTask);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sprintTask.getId().toString()))
            .body(sprintTask);
    }

    /**
     * {@code PATCH  /sprint-tasks/:id} : Partial updates given fields of an existing sprintTask, field will ignore if it is null
     *
     * @param id the id of the sprintTask to save.
     * @param sprintTask the sprintTask to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sprintTask,
     * or with status {@code 400 (Bad Request)} if the sprintTask is not valid,
     * or with status {@code 404 (Not Found)} if the sprintTask is not found,
     * or with status {@code 500 (Internal Server Error)} if the sprintTask couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SprintTask> partialUpdateSprintTask(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SprintTask sprintTask
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SprintTask partially : {}, {}", id, sprintTask);
        if (sprintTask.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sprintTask.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sprintTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SprintTask> result = sprintTaskService.partialUpdate(sprintTask);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sprintTask.getId().toString())
        );
    }

    /**
     * {@code GET  /sprint-tasks} : get all the sprintTasks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sprintTasks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SprintTask>> getAllSprintTasks(
        SprintTaskCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get SprintTasks by criteria: {}", criteria);

        Page<SprintTask> page = sprintTaskQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sprint-tasks/count} : count all the sprintTasks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSprintTasks(SprintTaskCriteria criteria) {
        LOG.debug("REST request to count SprintTasks by criteria: {}", criteria);
        return ResponseEntity.ok().body(sprintTaskQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sprint-tasks/:id} : get the "id" sprintTask.
     *
     * @param id the id of the sprintTask to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sprintTask, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SprintTask> getSprintTask(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SprintTask : {}", id);
        Optional<SprintTask> sprintTask = sprintTaskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sprintTask);
    }

    /**
     * {@code DELETE  /sprint-tasks/:id} : delete the "id" sprintTask.
     *
     * @param id the id of the sprintTask to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSprintTask(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SprintTask : {}", id);
        sprintTaskService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
