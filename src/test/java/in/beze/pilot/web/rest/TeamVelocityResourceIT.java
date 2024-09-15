package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.TeamVelocityAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.Sprint;
import in.beze.pilot.domain.TeamVelocity;
import in.beze.pilot.repository.TeamVelocityRepository;
import in.beze.pilot.service.TeamVelocityService;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TeamVelocityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TeamVelocityResourceIT {

    private static final Integer DEFAULT_SPRINT_VELOCITY = 1;
    private static final Integer UPDATED_SPRINT_VELOCITY = 2;
    private static final Integer SMALLER_SPRINT_VELOCITY = 1 - 1;

    private static final Integer DEFAULT_AVERAGE_VELOCITY = 1;
    private static final Integer UPDATED_AVERAGE_VELOCITY = 2;
    private static final Integer SMALLER_AVERAGE_VELOCITY = 1 - 1;

    private static final String ENTITY_API_URL = "/api/team-velocities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TeamVelocityRepository teamVelocityRepository;

    @Mock
    private TeamVelocityRepository teamVelocityRepositoryMock;

    @Mock
    private TeamVelocityService teamVelocityServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeamVelocityMockMvc;

    private TeamVelocity teamVelocity;

    private TeamVelocity insertedTeamVelocity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamVelocity createEntity() {
        return new TeamVelocity().sprintVelocity(DEFAULT_SPRINT_VELOCITY).averageVelocity(DEFAULT_AVERAGE_VELOCITY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamVelocity createUpdatedEntity() {
        return new TeamVelocity().sprintVelocity(UPDATED_SPRINT_VELOCITY).averageVelocity(UPDATED_AVERAGE_VELOCITY);
    }

    @BeforeEach
    public void initTest() {
        teamVelocity = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTeamVelocity != null) {
            teamVelocityRepository.delete(insertedTeamVelocity);
            insertedTeamVelocity = null;
        }
    }

    @Test
    @Transactional
    void createTeamVelocity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TeamVelocity
        var returnedTeamVelocity = om.readValue(
            restTeamVelocityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamVelocity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TeamVelocity.class
        );

        // Validate the TeamVelocity in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTeamVelocityUpdatableFieldsEquals(returnedTeamVelocity, getPersistedTeamVelocity(returnedTeamVelocity));

        insertedTeamVelocity = returnedTeamVelocity;
    }

    @Test
    @Transactional
    void createTeamVelocityWithExistingId() throws Exception {
        // Create the TeamVelocity with an existing ID
        teamVelocity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamVelocityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamVelocity)))
            .andExpect(status().isBadRequest());

        // Validate the TeamVelocity in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSprintVelocityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        teamVelocity.setSprintVelocity(null);

        // Create the TeamVelocity, which fails.

        restTeamVelocityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamVelocity)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAverageVelocityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        teamVelocity.setAverageVelocity(null);

        // Create the TeamVelocity, which fails.

        restTeamVelocityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamVelocity)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTeamVelocities() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList
        restTeamVelocityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teamVelocity.getId().intValue())))
            .andExpect(jsonPath("$.[*].sprintVelocity").value(hasItem(DEFAULT_SPRINT_VELOCITY)))
            .andExpect(jsonPath("$.[*].averageVelocity").value(hasItem(DEFAULT_AVERAGE_VELOCITY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeamVelocitiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(teamVelocityServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTeamVelocityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(teamVelocityServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeamVelocitiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(teamVelocityServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTeamVelocityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(teamVelocityRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTeamVelocity() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get the teamVelocity
        restTeamVelocityMockMvc
            .perform(get(ENTITY_API_URL_ID, teamVelocity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teamVelocity.getId().intValue()))
            .andExpect(jsonPath("$.sprintVelocity").value(DEFAULT_SPRINT_VELOCITY))
            .andExpect(jsonPath("$.averageVelocity").value(DEFAULT_AVERAGE_VELOCITY));
    }

    @Test
    @Transactional
    void getTeamVelocitiesByIdFiltering() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        Long id = teamVelocity.getId();

        defaultTeamVelocityFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTeamVelocityFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTeamVelocityFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesBySprintVelocityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where sprintVelocity equals to
        defaultTeamVelocityFiltering(
            "sprintVelocity.equals=" + DEFAULT_SPRINT_VELOCITY,
            "sprintVelocity.equals=" + UPDATED_SPRINT_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesBySprintVelocityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where sprintVelocity in
        defaultTeamVelocityFiltering(
            "sprintVelocity.in=" + DEFAULT_SPRINT_VELOCITY + "," + UPDATED_SPRINT_VELOCITY,
            "sprintVelocity.in=" + UPDATED_SPRINT_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesBySprintVelocityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where sprintVelocity is not null
        defaultTeamVelocityFiltering("sprintVelocity.specified=true", "sprintVelocity.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesBySprintVelocityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where sprintVelocity is greater than or equal to
        defaultTeamVelocityFiltering(
            "sprintVelocity.greaterThanOrEqual=" + DEFAULT_SPRINT_VELOCITY,
            "sprintVelocity.greaterThanOrEqual=" + UPDATED_SPRINT_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesBySprintVelocityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where sprintVelocity is less than or equal to
        defaultTeamVelocityFiltering(
            "sprintVelocity.lessThanOrEqual=" + DEFAULT_SPRINT_VELOCITY,
            "sprintVelocity.lessThanOrEqual=" + SMALLER_SPRINT_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesBySprintVelocityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where sprintVelocity is less than
        defaultTeamVelocityFiltering(
            "sprintVelocity.lessThan=" + UPDATED_SPRINT_VELOCITY,
            "sprintVelocity.lessThan=" + DEFAULT_SPRINT_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesBySprintVelocityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where sprintVelocity is greater than
        defaultTeamVelocityFiltering(
            "sprintVelocity.greaterThan=" + SMALLER_SPRINT_VELOCITY,
            "sprintVelocity.greaterThan=" + DEFAULT_SPRINT_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesByAverageVelocityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where averageVelocity equals to
        defaultTeamVelocityFiltering(
            "averageVelocity.equals=" + DEFAULT_AVERAGE_VELOCITY,
            "averageVelocity.equals=" + UPDATED_AVERAGE_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesByAverageVelocityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where averageVelocity in
        defaultTeamVelocityFiltering(
            "averageVelocity.in=" + DEFAULT_AVERAGE_VELOCITY + "," + UPDATED_AVERAGE_VELOCITY,
            "averageVelocity.in=" + UPDATED_AVERAGE_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesByAverageVelocityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where averageVelocity is not null
        defaultTeamVelocityFiltering("averageVelocity.specified=true", "averageVelocity.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesByAverageVelocityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where averageVelocity is greater than or equal to
        defaultTeamVelocityFiltering(
            "averageVelocity.greaterThanOrEqual=" + DEFAULT_AVERAGE_VELOCITY,
            "averageVelocity.greaterThanOrEqual=" + UPDATED_AVERAGE_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesByAverageVelocityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where averageVelocity is less than or equal to
        defaultTeamVelocityFiltering(
            "averageVelocity.lessThanOrEqual=" + DEFAULT_AVERAGE_VELOCITY,
            "averageVelocity.lessThanOrEqual=" + SMALLER_AVERAGE_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesByAverageVelocityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where averageVelocity is less than
        defaultTeamVelocityFiltering(
            "averageVelocity.lessThan=" + UPDATED_AVERAGE_VELOCITY,
            "averageVelocity.lessThan=" + DEFAULT_AVERAGE_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesByAverageVelocityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        // Get all the teamVelocityList where averageVelocity is greater than
        defaultTeamVelocityFiltering(
            "averageVelocity.greaterThan=" + SMALLER_AVERAGE_VELOCITY,
            "averageVelocity.greaterThan=" + DEFAULT_AVERAGE_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllTeamVelocitiesBySprintIsEqualToSomething() throws Exception {
        Sprint sprint;
        if (TestUtil.findAll(em, Sprint.class).isEmpty()) {
            teamVelocityRepository.saveAndFlush(teamVelocity);
            sprint = SprintResourceIT.createEntity();
        } else {
            sprint = TestUtil.findAll(em, Sprint.class).get(0);
        }
        em.persist(sprint);
        em.flush();
        teamVelocity.setSprint(sprint);
        teamVelocityRepository.saveAndFlush(teamVelocity);
        Long sprintId = sprint.getId();
        // Get all the teamVelocityList where sprint equals to sprintId
        defaultTeamVelocityShouldBeFound("sprintId.equals=" + sprintId);

        // Get all the teamVelocityList where sprint equals to (sprintId + 1)
        defaultTeamVelocityShouldNotBeFound("sprintId.equals=" + (sprintId + 1));
    }

    private void defaultTeamVelocityFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTeamVelocityShouldBeFound(shouldBeFound);
        defaultTeamVelocityShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTeamVelocityShouldBeFound(String filter) throws Exception {
        restTeamVelocityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teamVelocity.getId().intValue())))
            .andExpect(jsonPath("$.[*].sprintVelocity").value(hasItem(DEFAULT_SPRINT_VELOCITY)))
            .andExpect(jsonPath("$.[*].averageVelocity").value(hasItem(DEFAULT_AVERAGE_VELOCITY)));

        // Check, that the count call also returns 1
        restTeamVelocityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTeamVelocityShouldNotBeFound(String filter) throws Exception {
        restTeamVelocityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTeamVelocityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTeamVelocity() throws Exception {
        // Get the teamVelocity
        restTeamVelocityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTeamVelocity() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamVelocity
        TeamVelocity updatedTeamVelocity = teamVelocityRepository.findById(teamVelocity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTeamVelocity are not directly saved in db
        em.detach(updatedTeamVelocity);
        updatedTeamVelocity.sprintVelocity(UPDATED_SPRINT_VELOCITY).averageVelocity(UPDATED_AVERAGE_VELOCITY);

        restTeamVelocityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTeamVelocity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTeamVelocity))
            )
            .andExpect(status().isOk());

        // Validate the TeamVelocity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTeamVelocityToMatchAllProperties(updatedTeamVelocity);
    }

    @Test
    @Transactional
    void putNonExistingTeamVelocity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamVelocity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamVelocityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teamVelocity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teamVelocity))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamVelocity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeamVelocity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamVelocity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamVelocityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teamVelocity))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamVelocity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeamVelocity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamVelocity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamVelocityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamVelocity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamVelocity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeamVelocityWithPatch() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamVelocity using partial update
        TeamVelocity partialUpdatedTeamVelocity = new TeamVelocity();
        partialUpdatedTeamVelocity.setId(teamVelocity.getId());

        restTeamVelocityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamVelocity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTeamVelocity))
            )
            .andExpect(status().isOk());

        // Validate the TeamVelocity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamVelocityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTeamVelocity, teamVelocity),
            getPersistedTeamVelocity(teamVelocity)
        );
    }

    @Test
    @Transactional
    void fullUpdateTeamVelocityWithPatch() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamVelocity using partial update
        TeamVelocity partialUpdatedTeamVelocity = new TeamVelocity();
        partialUpdatedTeamVelocity.setId(teamVelocity.getId());

        partialUpdatedTeamVelocity.sprintVelocity(UPDATED_SPRINT_VELOCITY).averageVelocity(UPDATED_AVERAGE_VELOCITY);

        restTeamVelocityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamVelocity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTeamVelocity))
            )
            .andExpect(status().isOk());

        // Validate the TeamVelocity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamVelocityUpdatableFieldsEquals(partialUpdatedTeamVelocity, getPersistedTeamVelocity(partialUpdatedTeamVelocity));
    }

    @Test
    @Transactional
    void patchNonExistingTeamVelocity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamVelocity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamVelocityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teamVelocity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(teamVelocity))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamVelocity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeamVelocity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamVelocity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamVelocityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(teamVelocity))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamVelocity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeamVelocity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamVelocity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamVelocityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(teamVelocity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamVelocity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeamVelocity() throws Exception {
        // Initialize the database
        insertedTeamVelocity = teamVelocityRepository.saveAndFlush(teamVelocity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the teamVelocity
        restTeamVelocityMockMvc
            .perform(delete(ENTITY_API_URL_ID, teamVelocity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return teamVelocityRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TeamVelocity getPersistedTeamVelocity(TeamVelocity teamVelocity) {
        return teamVelocityRepository.findById(teamVelocity.getId()).orElseThrow();
    }

    protected void assertPersistedTeamVelocityToMatchAllProperties(TeamVelocity expectedTeamVelocity) {
        assertTeamVelocityAllPropertiesEquals(expectedTeamVelocity, getPersistedTeamVelocity(expectedTeamVelocity));
    }

    protected void assertPersistedTeamVelocityToMatchUpdatableProperties(TeamVelocity expectedTeamVelocity) {
        assertTeamVelocityAllUpdatablePropertiesEquals(expectedTeamVelocity, getPersistedTeamVelocity(expectedTeamVelocity));
    }
}
