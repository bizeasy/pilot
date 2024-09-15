package in.beze.pilot.web.rest;

import in.beze.pilot.domain.Sprint;
import in.beze.pilot.repository.SprintRepository;
import in.beze.pilot.service.SprintQueryService;
import in.beze.pilot.service.SprintService;
import in.beze.pilot.service.criteria.SprintCriteria;
import in.beze.pilot.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
 * REST controller for managing {@link in.beze.pilot.domain.Sprint}.
 */
@RestController
@RequestMapping("/api/sprints")
public class SprintResource {

    private static final Logger LOG = LoggerFactory.getLogger(SprintResource.class);

    private static final String ENTITY_NAME = "sprint";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SprintService sprintService;

    private final SprintRepository sprintRepository;

    private final SprintQueryService sprintQueryService;

    public SprintResource(SprintService sprintService, SprintRepository sprintRepository, SprintQueryService sprintQueryService) {
        this.sprintService = sprintService;
        this.sprintRepository = sprintRepository;
        this.sprintQueryService = sprintQueryService;
    }

    /**
     * {@code POST  /sprints} : Create a new sprint.
     *
     * @param sprint the sprint to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sprint, or with status {@code 400 (Bad Request)} if the sprint has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Sprint> createSprint(@Valid @RequestBody Sprint sprint) throws URISyntaxException {
        LOG.debug("REST request to save Sprint : {}", sprint);
        if (sprint.getId() != null) {
            throw new BadRequestAlertException("A new sprint cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sprint = sprintService.save(sprint);
        return ResponseEntity.created(new URI("/api/sprints/" + sprint.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, sprint.getId().toString()))
            .body(sprint);
    }

    /**
     * {@code PUT  /sprints/:id} : Updates an existing sprint.
     *
     * @param id the id of the sprint to save.
     * @param sprint the sprint to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sprint,
     * or with status {@code 400 (Bad Request)} if the sprint is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sprint couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Sprint> updateSprint(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Sprint sprint
    ) throws URISyntaxException {
        LOG.debug("REST request to update Sprint : {}, {}", id, sprint);
        if (sprint.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sprint.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sprintRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sprint = sprintService.update(sprint);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sprint.getId().toString()))
            .body(sprint);
    }

    /**
     * {@code PATCH  /sprints/:id} : Partial updates given fields of an existing sprint, field will ignore if it is null
     *
     * @param id the id of the sprint to save.
     * @param sprint the sprint to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sprint,
     * or with status {@code 400 (Bad Request)} if the sprint is not valid,
     * or with status {@code 404 (Not Found)} if the sprint is not found,
     * or with status {@code 500 (Internal Server Error)} if the sprint couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Sprint> partialUpdateSprint(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Sprint sprint
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Sprint partially : {}, {}", id, sprint);
        if (sprint.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sprint.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sprintRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Sprint> result = sprintService.partialUpdate(sprint);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sprint.getId().toString())
        );
    }

    /**
     * {@code GET  /sprints} : get all the sprints.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sprints in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Sprint>> getAllSprints(
        SprintCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Sprints by criteria: {}", criteria);

        Page<Sprint> page = sprintQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sprints/count} : count all the sprints.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSprints(SprintCriteria criteria) {
        LOG.debug("REST request to count Sprints by criteria: {}", criteria);
        return ResponseEntity.ok().body(sprintQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sprints/:id} : get the "id" sprint.
     *
     * @param id the id of the sprint to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sprint, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Sprint> getSprint(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Sprint : {}", id);
        Optional<Sprint> sprint = sprintService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sprint);
    }

    /**
     * {@code DELETE  /sprints/:id} : delete the "id" sprint.
     *
     * @param id the id of the sprint to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSprint(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Sprint : {}", id);
        sprintService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
