package in.beze.pilot.web.rest;

import in.beze.pilot.domain.Retrospective;
import in.beze.pilot.repository.RetrospectiveRepository;
import in.beze.pilot.service.RetrospectiveQueryService;
import in.beze.pilot.service.RetrospectiveService;
import in.beze.pilot.service.criteria.RetrospectiveCriteria;
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
 * REST controller for managing {@link in.beze.pilot.domain.Retrospective}.
 */
@RestController
@RequestMapping("/api/retrospectives")
public class RetrospectiveResource {

    private static final Logger LOG = LoggerFactory.getLogger(RetrospectiveResource.class);

    private static final String ENTITY_NAME = "retrospective";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RetrospectiveService retrospectiveService;

    private final RetrospectiveRepository retrospectiveRepository;

    private final RetrospectiveQueryService retrospectiveQueryService;

    public RetrospectiveResource(
        RetrospectiveService retrospectiveService,
        RetrospectiveRepository retrospectiveRepository,
        RetrospectiveQueryService retrospectiveQueryService
    ) {
        this.retrospectiveService = retrospectiveService;
        this.retrospectiveRepository = retrospectiveRepository;
        this.retrospectiveQueryService = retrospectiveQueryService;
    }

    /**
     * {@code POST  /retrospectives} : Create a new retrospective.
     *
     * @param retrospective the retrospective to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new retrospective, or with status {@code 400 (Bad Request)} if the retrospective has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Retrospective> createRetrospective(@Valid @RequestBody Retrospective retrospective) throws URISyntaxException {
        LOG.debug("REST request to save Retrospective : {}", retrospective);
        if (retrospective.getId() != null) {
            throw new BadRequestAlertException("A new retrospective cannot already have an ID", ENTITY_NAME, "idexists");
        }
        retrospective = retrospectiveService.save(retrospective);
        return ResponseEntity.created(new URI("/api/retrospectives/" + retrospective.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, retrospective.getId().toString()))
            .body(retrospective);
    }

    /**
     * {@code PUT  /retrospectives/:id} : Updates an existing retrospective.
     *
     * @param id the id of the retrospective to save.
     * @param retrospective the retrospective to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated retrospective,
     * or with status {@code 400 (Bad Request)} if the retrospective is not valid,
     * or with status {@code 500 (Internal Server Error)} if the retrospective couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Retrospective> updateRetrospective(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Retrospective retrospective
    ) throws URISyntaxException {
        LOG.debug("REST request to update Retrospective : {}, {}", id, retrospective);
        if (retrospective.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, retrospective.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!retrospectiveRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        retrospective = retrospectiveService.update(retrospective);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, retrospective.getId().toString()))
            .body(retrospective);
    }

    /**
     * {@code PATCH  /retrospectives/:id} : Partial updates given fields of an existing retrospective, field will ignore if it is null
     *
     * @param id the id of the retrospective to save.
     * @param retrospective the retrospective to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated retrospective,
     * or with status {@code 400 (Bad Request)} if the retrospective is not valid,
     * or with status {@code 404 (Not Found)} if the retrospective is not found,
     * or with status {@code 500 (Internal Server Error)} if the retrospective couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Retrospective> partialUpdateRetrospective(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Retrospective retrospective
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Retrospective partially : {}, {}", id, retrospective);
        if (retrospective.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, retrospective.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!retrospectiveRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Retrospective> result = retrospectiveService.partialUpdate(retrospective);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, retrospective.getId().toString())
        );
    }

    /**
     * {@code GET  /retrospectives} : get all the retrospectives.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of retrospectives in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Retrospective>> getAllRetrospectives(
        RetrospectiveCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Retrospectives by criteria: {}", criteria);

        Page<Retrospective> page = retrospectiveQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /retrospectives/count} : count all the retrospectives.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRetrospectives(RetrospectiveCriteria criteria) {
        LOG.debug("REST request to count Retrospectives by criteria: {}", criteria);
        return ResponseEntity.ok().body(retrospectiveQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /retrospectives/:id} : get the "id" retrospective.
     *
     * @param id the id of the retrospective to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the retrospective, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Retrospective> getRetrospective(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Retrospective : {}", id);
        Optional<Retrospective> retrospective = retrospectiveService.findOne(id);
        return ResponseUtil.wrapOrNotFound(retrospective);
    }

    /**
     * {@code DELETE  /retrospectives/:id} : delete the "id" retrospective.
     *
     * @param id the id of the retrospective to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRetrospective(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Retrospective : {}", id);
        retrospectiveService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
