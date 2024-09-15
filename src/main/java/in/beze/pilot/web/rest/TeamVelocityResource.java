package in.beze.pilot.web.rest;

import in.beze.pilot.domain.TeamVelocity;
import in.beze.pilot.repository.TeamVelocityRepository;
import in.beze.pilot.service.TeamVelocityQueryService;
import in.beze.pilot.service.TeamVelocityService;
import in.beze.pilot.service.criteria.TeamVelocityCriteria;
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
 * REST controller for managing {@link in.beze.pilot.domain.TeamVelocity}.
 */
@RestController
@RequestMapping("/api/team-velocities")
public class TeamVelocityResource {

    private static final Logger LOG = LoggerFactory.getLogger(TeamVelocityResource.class);

    private static final String ENTITY_NAME = "teamVelocity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeamVelocityService teamVelocityService;

    private final TeamVelocityRepository teamVelocityRepository;

    private final TeamVelocityQueryService teamVelocityQueryService;

    public TeamVelocityResource(
        TeamVelocityService teamVelocityService,
        TeamVelocityRepository teamVelocityRepository,
        TeamVelocityQueryService teamVelocityQueryService
    ) {
        this.teamVelocityService = teamVelocityService;
        this.teamVelocityRepository = teamVelocityRepository;
        this.teamVelocityQueryService = teamVelocityQueryService;
    }

    /**
     * {@code POST  /team-velocities} : Create a new teamVelocity.
     *
     * @param teamVelocity the teamVelocity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teamVelocity, or with status {@code 400 (Bad Request)} if the teamVelocity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TeamVelocity> createTeamVelocity(@Valid @RequestBody TeamVelocity teamVelocity) throws URISyntaxException {
        LOG.debug("REST request to save TeamVelocity : {}", teamVelocity);
        if (teamVelocity.getId() != null) {
            throw new BadRequestAlertException("A new teamVelocity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        teamVelocity = teamVelocityService.save(teamVelocity);
        return ResponseEntity.created(new URI("/api/team-velocities/" + teamVelocity.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, teamVelocity.getId().toString()))
            .body(teamVelocity);
    }

    /**
     * {@code PUT  /team-velocities/:id} : Updates an existing teamVelocity.
     *
     * @param id the id of the teamVelocity to save.
     * @param teamVelocity the teamVelocity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamVelocity,
     * or with status {@code 400 (Bad Request)} if the teamVelocity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teamVelocity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TeamVelocity> updateTeamVelocity(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TeamVelocity teamVelocity
    ) throws URISyntaxException {
        LOG.debug("REST request to update TeamVelocity : {}, {}", id, teamVelocity);
        if (teamVelocity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamVelocity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teamVelocityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        teamVelocity = teamVelocityService.update(teamVelocity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teamVelocity.getId().toString()))
            .body(teamVelocity);
    }

    /**
     * {@code PATCH  /team-velocities/:id} : Partial updates given fields of an existing teamVelocity, field will ignore if it is null
     *
     * @param id the id of the teamVelocity to save.
     * @param teamVelocity the teamVelocity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamVelocity,
     * or with status {@code 400 (Bad Request)} if the teamVelocity is not valid,
     * or with status {@code 404 (Not Found)} if the teamVelocity is not found,
     * or with status {@code 500 (Internal Server Error)} if the teamVelocity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TeamVelocity> partialUpdateTeamVelocity(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TeamVelocity teamVelocity
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TeamVelocity partially : {}, {}", id, teamVelocity);
        if (teamVelocity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamVelocity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teamVelocityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TeamVelocity> result = teamVelocityService.partialUpdate(teamVelocity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teamVelocity.getId().toString())
        );
    }

    /**
     * {@code GET  /team-velocities} : get all the teamVelocities.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teamVelocities in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TeamVelocity>> getAllTeamVelocities(
        TeamVelocityCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TeamVelocities by criteria: {}", criteria);

        Page<TeamVelocity> page = teamVelocityQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /team-velocities/count} : count all the teamVelocities.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTeamVelocities(TeamVelocityCriteria criteria) {
        LOG.debug("REST request to count TeamVelocities by criteria: {}", criteria);
        return ResponseEntity.ok().body(teamVelocityQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /team-velocities/:id} : get the "id" teamVelocity.
     *
     * @param id the id of the teamVelocity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teamVelocity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeamVelocity> getTeamVelocity(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TeamVelocity : {}", id);
        Optional<TeamVelocity> teamVelocity = teamVelocityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teamVelocity);
    }

    /**
     * {@code DELETE  /team-velocities/:id} : delete the "id" teamVelocity.
     *
     * @param id the id of the teamVelocity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamVelocity(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TeamVelocity : {}", id);
        teamVelocityService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
