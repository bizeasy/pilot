package in.beze.pilot.web.rest;

import in.beze.pilot.domain.PartyType;
import in.beze.pilot.repository.PartyTypeRepository;
import in.beze.pilot.service.PartyTypeQueryService;
import in.beze.pilot.service.PartyTypeService;
import in.beze.pilot.service.criteria.PartyTypeCriteria;
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
 * REST controller for managing {@link in.beze.pilot.domain.PartyType}.
 */
@RestController
@RequestMapping("/api/party-types")
public class PartyTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(PartyTypeResource.class);

    private static final String ENTITY_NAME = "partyType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PartyTypeService partyTypeService;

    private final PartyTypeRepository partyTypeRepository;

    private final PartyTypeQueryService partyTypeQueryService;

    public PartyTypeResource(
        PartyTypeService partyTypeService,
        PartyTypeRepository partyTypeRepository,
        PartyTypeQueryService partyTypeQueryService
    ) {
        this.partyTypeService = partyTypeService;
        this.partyTypeRepository = partyTypeRepository;
        this.partyTypeQueryService = partyTypeQueryService;
    }

    /**
     * {@code POST  /party-types} : Create a new partyType.
     *
     * @param partyType the partyType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new partyType, or with status {@code 400 (Bad Request)} if the partyType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PartyType> createPartyType(@Valid @RequestBody PartyType partyType) throws URISyntaxException {
        LOG.debug("REST request to save PartyType : {}", partyType);
        if (partyType.getId() != null) {
            throw new BadRequestAlertException("A new partyType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        partyType = partyTypeService.save(partyType);
        return ResponseEntity.created(new URI("/api/party-types/" + partyType.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, partyType.getId().toString()))
            .body(partyType);
    }

    /**
     * {@code PUT  /party-types/:id} : Updates an existing partyType.
     *
     * @param id the id of the partyType to save.
     * @param partyType the partyType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partyType,
     * or with status {@code 400 (Bad Request)} if the partyType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the partyType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PartyType> updatePartyType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PartyType partyType
    ) throws URISyntaxException {
        LOG.debug("REST request to update PartyType : {}, {}", id, partyType);
        if (partyType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partyType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partyTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        partyType = partyTypeService.update(partyType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, partyType.getId().toString()))
            .body(partyType);
    }

    /**
     * {@code PATCH  /party-types/:id} : Partial updates given fields of an existing partyType, field will ignore if it is null
     *
     * @param id the id of the partyType to save.
     * @param partyType the partyType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partyType,
     * or with status {@code 400 (Bad Request)} if the partyType is not valid,
     * or with status {@code 404 (Not Found)} if the partyType is not found,
     * or with status {@code 500 (Internal Server Error)} if the partyType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PartyType> partialUpdatePartyType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PartyType partyType
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PartyType partially : {}, {}", id, partyType);
        if (partyType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partyType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partyTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PartyType> result = partyTypeService.partialUpdate(partyType);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, partyType.getId().toString())
        );
    }

    /**
     * {@code GET  /party-types} : get all the partyTypes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of partyTypes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PartyType>> getAllPartyTypes(
        PartyTypeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PartyTypes by criteria: {}", criteria);

        Page<PartyType> page = partyTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /party-types/count} : count all the partyTypes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPartyTypes(PartyTypeCriteria criteria) {
        LOG.debug("REST request to count PartyTypes by criteria: {}", criteria);
        return ResponseEntity.ok().body(partyTypeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /party-types/:id} : get the "id" partyType.
     *
     * @param id the id of the partyType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the partyType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PartyType> getPartyType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PartyType : {}", id);
        Optional<PartyType> partyType = partyTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(partyType);
    }

    /**
     * {@code DELETE  /party-types/:id} : delete the "id" partyType.
     *
     * @param id the id of the partyType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartyType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PartyType : {}", id);
        partyTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
