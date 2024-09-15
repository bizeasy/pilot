package in.beze.pilot.web.rest;

import in.beze.pilot.domain.SprintTaskHistory;
import in.beze.pilot.repository.SprintTaskHistoryRepository;
import in.beze.pilot.service.SprintTaskHistoryQueryService;
import in.beze.pilot.service.SprintTaskHistoryService;
import in.beze.pilot.service.criteria.SprintTaskHistoryCriteria;
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
 * REST controller for managing {@link in.beze.pilot.domain.SprintTaskHistory}.
 */
@RestController
@RequestMapping("/api/sprint-task-histories")
public class SprintTaskHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(SprintTaskHistoryResource.class);

    private static final String ENTITY_NAME = "sprintTaskHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SprintTaskHistoryService sprintTaskHistoryService;

    private final SprintTaskHistoryRepository sprintTaskHistoryRepository;

    private final SprintTaskHistoryQueryService sprintTaskHistoryQueryService;

    public SprintTaskHistoryResource(
        SprintTaskHistoryService sprintTaskHistoryService,
        SprintTaskHistoryRepository sprintTaskHistoryRepository,
        SprintTaskHistoryQueryService sprintTaskHistoryQueryService
    ) {
        this.sprintTaskHistoryService = sprintTaskHistoryService;
        this.sprintTaskHistoryRepository = sprintTaskHistoryRepository;
        this.sprintTaskHistoryQueryService = sprintTaskHistoryQueryService;
    }

    /**
     * {@code POST  /sprint-task-histories} : Create a new sprintTaskHistory.
     *
     * @param sprintTaskHistory the sprintTaskHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sprintTaskHistory, or with status {@code 400 (Bad Request)} if the sprintTaskHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SprintTaskHistory> createSprintTaskHistory(@RequestBody SprintTaskHistory sprintTaskHistory)
        throws URISyntaxException {
        LOG.debug("REST request to save SprintTaskHistory : {}", sprintTaskHistory);
        if (sprintTaskHistory.getId() != null) {
            throw new BadRequestAlertException("A new sprintTaskHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sprintTaskHistory = sprintTaskHistoryService.save(sprintTaskHistory);
        return ResponseEntity.created(new URI("/api/sprint-task-histories/" + sprintTaskHistory.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, sprintTaskHistory.getId().toString()))
            .body(sprintTaskHistory);
    }

    /**
     * {@code PUT  /sprint-task-histories/:id} : Updates an existing sprintTaskHistory.
     *
     * @param id the id of the sprintTaskHistory to save.
     * @param sprintTaskHistory the sprintTaskHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sprintTaskHistory,
     * or with status {@code 400 (Bad Request)} if the sprintTaskHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sprintTaskHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SprintTaskHistory> updateSprintTaskHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SprintTaskHistory sprintTaskHistory
    ) throws URISyntaxException {
        LOG.debug("REST request to update SprintTaskHistory : {}, {}", id, sprintTaskHistory);
        if (sprintTaskHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sprintTaskHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sprintTaskHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sprintTaskHistory = sprintTaskHistoryService.update(sprintTaskHistory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sprintTaskHistory.getId().toString()))
            .body(sprintTaskHistory);
    }

    /**
     * {@code PATCH  /sprint-task-histories/:id} : Partial updates given fields of an existing sprintTaskHistory, field will ignore if it is null
     *
     * @param id the id of the sprintTaskHistory to save.
     * @param sprintTaskHistory the sprintTaskHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sprintTaskHistory,
     * or with status {@code 400 (Bad Request)} if the sprintTaskHistory is not valid,
     * or with status {@code 404 (Not Found)} if the sprintTaskHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the sprintTaskHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SprintTaskHistory> partialUpdateSprintTaskHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SprintTaskHistory sprintTaskHistory
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SprintTaskHistory partially : {}, {}", id, sprintTaskHistory);
        if (sprintTaskHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sprintTaskHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sprintTaskHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SprintTaskHistory> result = sprintTaskHistoryService.partialUpdate(sprintTaskHistory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sprintTaskHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /sprint-task-histories} : get all the sprintTaskHistories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sprintTaskHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SprintTaskHistory>> getAllSprintTaskHistories(
        SprintTaskHistoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get SprintTaskHistories by criteria: {}", criteria);

        Page<SprintTaskHistory> page = sprintTaskHistoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sprint-task-histories/count} : count all the sprintTaskHistories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSprintTaskHistories(SprintTaskHistoryCriteria criteria) {
        LOG.debug("REST request to count SprintTaskHistories by criteria: {}", criteria);
        return ResponseEntity.ok().body(sprintTaskHistoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sprint-task-histories/:id} : get the "id" sprintTaskHistory.
     *
     * @param id the id of the sprintTaskHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sprintTaskHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SprintTaskHistory> getSprintTaskHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SprintTaskHistory : {}", id);
        Optional<SprintTaskHistory> sprintTaskHistory = sprintTaskHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sprintTaskHistory);
    }

    /**
     * {@code DELETE  /sprint-task-histories/:id} : delete the "id" sprintTaskHistory.
     *
     * @param id the id of the sprintTaskHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSprintTaskHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SprintTaskHistory : {}", id);
        sprintTaskHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
