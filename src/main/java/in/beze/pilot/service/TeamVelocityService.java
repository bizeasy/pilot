package in.beze.pilot.service;

import in.beze.pilot.domain.TeamVelocity;
import in.beze.pilot.repository.TeamVelocityRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link in.beze.pilot.domain.TeamVelocity}.
 */
@Service
@Transactional
public class TeamVelocityService {

    private static final Logger LOG = LoggerFactory.getLogger(TeamVelocityService.class);

    private final TeamVelocityRepository teamVelocityRepository;

    public TeamVelocityService(TeamVelocityRepository teamVelocityRepository) {
        this.teamVelocityRepository = teamVelocityRepository;
    }

    /**
     * Save a teamVelocity.
     *
     * @param teamVelocity the entity to save.
     * @return the persisted entity.
     */
    public TeamVelocity save(TeamVelocity teamVelocity) {
        LOG.debug("Request to save TeamVelocity : {}", teamVelocity);
        return teamVelocityRepository.save(teamVelocity);
    }

    /**
     * Update a teamVelocity.
     *
     * @param teamVelocity the entity to save.
     * @return the persisted entity.
     */
    public TeamVelocity update(TeamVelocity teamVelocity) {
        LOG.debug("Request to update TeamVelocity : {}", teamVelocity);
        return teamVelocityRepository.save(teamVelocity);
    }

    /**
     * Partially update a teamVelocity.
     *
     * @param teamVelocity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TeamVelocity> partialUpdate(TeamVelocity teamVelocity) {
        LOG.debug("Request to partially update TeamVelocity : {}", teamVelocity);

        return teamVelocityRepository
            .findById(teamVelocity.getId())
            .map(existingTeamVelocity -> {
                if (teamVelocity.getSprintVelocity() != null) {
                    existingTeamVelocity.setSprintVelocity(teamVelocity.getSprintVelocity());
                }
                if (teamVelocity.getAverageVelocity() != null) {
                    existingTeamVelocity.setAverageVelocity(teamVelocity.getAverageVelocity());
                }

                return existingTeamVelocity;
            })
            .map(teamVelocityRepository::save);
    }

    /**
     * Get all the teamVelocities with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TeamVelocity> findAllWithEagerRelationships(Pageable pageable) {
        return teamVelocityRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one teamVelocity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TeamVelocity> findOne(Long id) {
        LOG.debug("Request to get TeamVelocity : {}", id);
        return teamVelocityRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the teamVelocity by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TeamVelocity : {}", id);
        teamVelocityRepository.deleteById(id);
    }
}
