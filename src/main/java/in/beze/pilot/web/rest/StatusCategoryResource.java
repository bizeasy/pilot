package in.beze.pilot.web.rest;

import in.beze.pilot.domain.StatusCategory;
import in.beze.pilot.repository.StatusCategoryRepository;
import in.beze.pilot.service.StatusCategoryQueryService;
import in.beze.pilot.service.StatusCategoryService;
import in.beze.pilot.service.criteria.StatusCategoryCriteria;
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
 * REST controller for managing {@link in.beze.pilot.domain.StatusCategory}.
 */
@RestController
@RequestMapping("/api/status-categories")
public class StatusCategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(StatusCategoryResource.class);

    private static final String ENTITY_NAME = "statusCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StatusCategoryService statusCategoryService;

    private final StatusCategoryRepository statusCategoryRepository;

    private final StatusCategoryQueryService statusCategoryQueryService;

    public StatusCategoryResource(
        StatusCategoryService statusCategoryService,
        StatusCategoryRepository statusCategoryRepository,
        StatusCategoryQueryService statusCategoryQueryService
    ) {
        this.statusCategoryService = statusCategoryService;
        this.statusCategoryRepository = statusCategoryRepository;
        this.statusCategoryQueryService = statusCategoryQueryService;
    }

    /**
     * {@code POST  /status-categories} : Create a new statusCategory.
     *
     * @param statusCategory the statusCategory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new statusCategory, or with status {@code 400 (Bad Request)} if the statusCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StatusCategory> createStatusCategory(@Valid @RequestBody StatusCategory statusCategory)
        throws URISyntaxException {
        LOG.debug("REST request to save StatusCategory : {}", statusCategory);
        if (statusCategory.getId() != null) {
            throw new BadRequestAlertException("A new statusCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        statusCategory = statusCategoryService.save(statusCategory);
        return ResponseEntity.created(new URI("/api/status-categories/" + statusCategory.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, statusCategory.getId().toString()))
            .body(statusCategory);
    }

    /**
     * {@code PUT  /status-categories/:id} : Updates an existing statusCategory.
     *
     * @param id the id of the statusCategory to save.
     * @param statusCategory the statusCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statusCategory,
     * or with status {@code 400 (Bad Request)} if the statusCategory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the statusCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StatusCategory> updateStatusCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StatusCategory statusCategory
    ) throws URISyntaxException {
        LOG.debug("REST request to update StatusCategory : {}, {}", id, statusCategory);
        if (statusCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statusCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statusCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        statusCategory = statusCategoryService.update(statusCategory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statusCategory.getId().toString()))
            .body(statusCategory);
    }

    /**
     * {@code PATCH  /status-categories/:id} : Partial updates given fields of an existing statusCategory, field will ignore if it is null
     *
     * @param id the id of the statusCategory to save.
     * @param statusCategory the statusCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statusCategory,
     * or with status {@code 400 (Bad Request)} if the statusCategory is not valid,
     * or with status {@code 404 (Not Found)} if the statusCategory is not found,
     * or with status {@code 500 (Internal Server Error)} if the statusCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StatusCategory> partialUpdateStatusCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StatusCategory statusCategory
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StatusCategory partially : {}, {}", id, statusCategory);
        if (statusCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statusCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statusCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StatusCategory> result = statusCategoryService.partialUpdate(statusCategory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statusCategory.getId().toString())
        );
    }

    /**
     * {@code GET  /status-categories} : get all the statusCategories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statusCategories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StatusCategory>> getAllStatusCategories(
        StatusCategoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get StatusCategories by criteria: {}", criteria);

        Page<StatusCategory> page = statusCategoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /status-categories/count} : count all the statusCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStatusCategories(StatusCategoryCriteria criteria) {
        LOG.debug("REST request to count StatusCategories by criteria: {}", criteria);
        return ResponseEntity.ok().body(statusCategoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /status-categories/:id} : get the "id" statusCategory.
     *
     * @param id the id of the statusCategory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statusCategory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StatusCategory> getStatusCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StatusCategory : {}", id);
        Optional<StatusCategory> statusCategory = statusCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(statusCategory);
    }

    /**
     * {@code DELETE  /status-categories/:id} : delete the "id" statusCategory.
     *
     * @param id the id of the statusCategory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatusCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StatusCategory : {}", id);
        statusCategoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
