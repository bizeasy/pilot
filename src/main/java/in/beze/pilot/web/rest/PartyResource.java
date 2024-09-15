package in.beze.pilot.web.rest;

import in.beze.pilot.domain.Party;
import in.beze.pilot.repository.PartyRepository;
import in.beze.pilot.service.PartyQueryService;
import in.beze.pilot.service.PartyService;
import in.beze.pilot.service.criteria.PartyCriteria;
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
 * REST controller for managing {@link in.beze.pilot.domain.Party}.
 */
@RestController
@RequestMapping("/api/parties")
public class PartyResource {

    private static final Logger LOG = LoggerFactory.getLogger(PartyResource.class);

    private static final String ENTITY_NAME = "party";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PartyService partyService;

    private final PartyRepository partyRepository;

    private final PartyQueryService partyQueryService;

    public PartyResource(PartyService partyService, PartyRepository partyRepository, PartyQueryService partyQueryService) {
        this.partyService = partyService;
        this.partyRepository = partyRepository;
        this.partyQueryService = partyQueryService;
    }

    /**
     * {@code POST  /parties} : Create a new party.
     *
     * @param party the party to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new party, or with status {@code 400 (Bad Request)} if the party has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Party> createParty(@Valid @RequestBody Party party) throws URISyntaxException {
        LOG.debug("REST request to save Party : {}", party);
        if (party.getId() != null) {
            throw new BadRequestAlertException("A new party cannot already have an ID", ENTITY_NAME, "idexists");
        }
        party = partyService.save(party);
        return ResponseEntity.created(new URI("/api/parties/" + party.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, party.getId().toString()))
            .body(party);
    }

    /**
     * {@code PUT  /parties/:id} : Updates an existing party.
     *
     * @param id the id of the party to save.
     * @param party the party to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated party,
     * or with status {@code 400 (Bad Request)} if the party is not valid,
     * or with status {@code 500 (Internal Server Error)} if the party couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Party> updateParty(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Party party)
        throws URISyntaxException {
        LOG.debug("REST request to update Party : {}, {}", id, party);
        if (party.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, party.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        party = partyService.update(party);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, party.getId().toString()))
            .body(party);
    }

    /**
     * {@code PATCH  /parties/:id} : Partial updates given fields of an existing party, field will ignore if it is null
     *
     * @param id the id of the party to save.
     * @param party the party to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated party,
     * or with status {@code 400 (Bad Request)} if the party is not valid,
     * or with status {@code 404 (Not Found)} if the party is not found,
     * or with status {@code 500 (Internal Server Error)} if the party couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Party> partialUpdateParty(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Party party
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Party partially : {}, {}", id, party);
        if (party.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, party.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Party> result = partyService.partialUpdate(party);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, party.getId().toString())
        );
    }

    /**
     * {@code GET  /parties} : get all the parties.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of parties in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Party>> getAllParties(
        PartyCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Parties by criteria: {}", criteria);

        Page<Party> page = partyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /parties/count} : count all the parties.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countParties(PartyCriteria criteria) {
        LOG.debug("REST request to count Parties by criteria: {}", criteria);
        return ResponseEntity.ok().body(partyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /parties/:id} : get the "id" party.
     *
     * @param id the id of the party to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the party, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Party> getParty(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Party : {}", id);
        Optional<Party> party = partyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(party);
    }

    /**
     * {@code DELETE  /parties/:id} : delete the "id" party.
     *
     * @param id the id of the party to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParty(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Party : {}", id);
        partyService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
