package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.IndividualPerformanceAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.IndividualPerformance;
import in.beze.pilot.domain.Party;
import in.beze.pilot.domain.Sprint;
import in.beze.pilot.repository.IndividualPerformanceRepository;
import in.beze.pilot.service.IndividualPerformanceService;
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
 * Integration tests for the {@link IndividualPerformanceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class IndividualPerformanceResourceIT {

    private static final Integer DEFAULT_COMPLETED_TASKS = 1;
    private static final Integer UPDATED_COMPLETED_TASKS = 2;
    private static final Integer SMALLER_COMPLETED_TASKS = 1 - 1;

    private static final Integer DEFAULT_VELOCITY = 1;
    private static final Integer UPDATED_VELOCITY = 2;
    private static final Integer SMALLER_VELOCITY = 1 - 1;

    private static final Integer DEFAULT_STORY_POINTS_COMPLETED = 1;
    private static final Integer UPDATED_STORY_POINTS_COMPLETED = 2;
    private static final Integer SMALLER_STORY_POINTS_COMPLETED = 1 - 1;

    private static final String ENTITY_API_URL = "/api/individual-performances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IndividualPerformanceRepository individualPerformanceRepository;

    @Mock
    private IndividualPerformanceRepository individualPerformanceRepositoryMock;

    @Mock
    private IndividualPerformanceService individualPerformanceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIndividualPerformanceMockMvc;

    private IndividualPerformance individualPerformance;

    private IndividualPerformance insertedIndividualPerformance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IndividualPerformance createEntity() {
        return new IndividualPerformance()
            .completedTasks(DEFAULT_COMPLETED_TASKS)
            .velocity(DEFAULT_VELOCITY)
            .storyPointsCompleted(DEFAULT_STORY_POINTS_COMPLETED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IndividualPerformance createUpdatedEntity() {
        return new IndividualPerformance()
            .completedTasks(UPDATED_COMPLETED_TASKS)
            .velocity(UPDATED_VELOCITY)
            .storyPointsCompleted(UPDATED_STORY_POINTS_COMPLETED);
    }

    @BeforeEach
    public void initTest() {
        individualPerformance = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedIndividualPerformance != null) {
            individualPerformanceRepository.delete(insertedIndividualPerformance);
            insertedIndividualPerformance = null;
        }
    }

    @Test
    @Transactional
    void createIndividualPerformance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the IndividualPerformance
        var returnedIndividualPerformance = om.readValue(
            restIndividualPerformanceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(individualPerformance)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            IndividualPerformance.class
        );

        // Validate the IndividualPerformance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertIndividualPerformanceUpdatableFieldsEquals(
            returnedIndividualPerformance,
            getPersistedIndividualPerformance(returnedIndividualPerformance)
        );

        insertedIndividualPerformance = returnedIndividualPerformance;
    }

    @Test
    @Transactional
    void createIndividualPerformanceWithExistingId() throws Exception {
        // Create the IndividualPerformance with an existing ID
        individualPerformance.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIndividualPerformanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(individualPerformance)))
            .andExpect(status().isBadRequest());

        // Validate the IndividualPerformance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllIndividualPerformances() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList
        restIndividualPerformanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(individualPerformance.getId().intValue())))
            .andExpect(jsonPath("$.[*].completedTasks").value(hasItem(DEFAULT_COMPLETED_TASKS)))
            .andExpect(jsonPath("$.[*].velocity").value(hasItem(DEFAULT_VELOCITY)))
            .andExpect(jsonPath("$.[*].storyPointsCompleted").value(hasItem(DEFAULT_STORY_POINTS_COMPLETED)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIndividualPerformancesWithEagerRelationshipsIsEnabled() throws Exception {
        when(individualPerformanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIndividualPerformanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(individualPerformanceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIndividualPerformancesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(individualPerformanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIndividualPerformanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(individualPerformanceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getIndividualPerformance() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get the individualPerformance
        restIndividualPerformanceMockMvc
            .perform(get(ENTITY_API_URL_ID, individualPerformance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(individualPerformance.getId().intValue()))
            .andExpect(jsonPath("$.completedTasks").value(DEFAULT_COMPLETED_TASKS))
            .andExpect(jsonPath("$.velocity").value(DEFAULT_VELOCITY))
            .andExpect(jsonPath("$.storyPointsCompleted").value(DEFAULT_STORY_POINTS_COMPLETED));
    }

    @Test
    @Transactional
    void getIndividualPerformancesByIdFiltering() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        Long id = individualPerformance.getId();

        defaultIndividualPerformanceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultIndividualPerformanceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultIndividualPerformanceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByCompletedTasksIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where completedTasks equals to
        defaultIndividualPerformanceFiltering(
            "completedTasks.equals=" + DEFAULT_COMPLETED_TASKS,
            "completedTasks.equals=" + UPDATED_COMPLETED_TASKS
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByCompletedTasksIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where completedTasks in
        defaultIndividualPerformanceFiltering(
            "completedTasks.in=" + DEFAULT_COMPLETED_TASKS + "," + UPDATED_COMPLETED_TASKS,
            "completedTasks.in=" + UPDATED_COMPLETED_TASKS
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByCompletedTasksIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where completedTasks is not null
        defaultIndividualPerformanceFiltering("completedTasks.specified=true", "completedTasks.specified=false");
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByCompletedTasksIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where completedTasks is greater than or equal to
        defaultIndividualPerformanceFiltering(
            "completedTasks.greaterThanOrEqual=" + DEFAULT_COMPLETED_TASKS,
            "completedTasks.greaterThanOrEqual=" + UPDATED_COMPLETED_TASKS
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByCompletedTasksIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where completedTasks is less than or equal to
        defaultIndividualPerformanceFiltering(
            "completedTasks.lessThanOrEqual=" + DEFAULT_COMPLETED_TASKS,
            "completedTasks.lessThanOrEqual=" + SMALLER_COMPLETED_TASKS
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByCompletedTasksIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where completedTasks is less than
        defaultIndividualPerformanceFiltering(
            "completedTasks.lessThan=" + UPDATED_COMPLETED_TASKS,
            "completedTasks.lessThan=" + DEFAULT_COMPLETED_TASKS
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByCompletedTasksIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where completedTasks is greater than
        defaultIndividualPerformanceFiltering(
            "completedTasks.greaterThan=" + SMALLER_COMPLETED_TASKS,
            "completedTasks.greaterThan=" + DEFAULT_COMPLETED_TASKS
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByVelocityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where velocity equals to
        defaultIndividualPerformanceFiltering("velocity.equals=" + DEFAULT_VELOCITY, "velocity.equals=" + UPDATED_VELOCITY);
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByVelocityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where velocity in
        defaultIndividualPerformanceFiltering(
            "velocity.in=" + DEFAULT_VELOCITY + "," + UPDATED_VELOCITY,
            "velocity.in=" + UPDATED_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByVelocityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where velocity is not null
        defaultIndividualPerformanceFiltering("velocity.specified=true", "velocity.specified=false");
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByVelocityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where velocity is greater than or equal to
        defaultIndividualPerformanceFiltering(
            "velocity.greaterThanOrEqual=" + DEFAULT_VELOCITY,
            "velocity.greaterThanOrEqual=" + UPDATED_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByVelocityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where velocity is less than or equal to
        defaultIndividualPerformanceFiltering(
            "velocity.lessThanOrEqual=" + DEFAULT_VELOCITY,
            "velocity.lessThanOrEqual=" + SMALLER_VELOCITY
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByVelocityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where velocity is less than
        defaultIndividualPerformanceFiltering("velocity.lessThan=" + UPDATED_VELOCITY, "velocity.lessThan=" + DEFAULT_VELOCITY);
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByVelocityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where velocity is greater than
        defaultIndividualPerformanceFiltering("velocity.greaterThan=" + SMALLER_VELOCITY, "velocity.greaterThan=" + DEFAULT_VELOCITY);
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByStoryPointsCompletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where storyPointsCompleted equals to
        defaultIndividualPerformanceFiltering(
            "storyPointsCompleted.equals=" + DEFAULT_STORY_POINTS_COMPLETED,
            "storyPointsCompleted.equals=" + UPDATED_STORY_POINTS_COMPLETED
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByStoryPointsCompletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where storyPointsCompleted in
        defaultIndividualPerformanceFiltering(
            "storyPointsCompleted.in=" + DEFAULT_STORY_POINTS_COMPLETED + "," + UPDATED_STORY_POINTS_COMPLETED,
            "storyPointsCompleted.in=" + UPDATED_STORY_POINTS_COMPLETED
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByStoryPointsCompletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where storyPointsCompleted is not null
        defaultIndividualPerformanceFiltering("storyPointsCompleted.specified=true", "storyPointsCompleted.specified=false");
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByStoryPointsCompletedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where storyPointsCompleted is greater than or equal to
        defaultIndividualPerformanceFiltering(
            "storyPointsCompleted.greaterThanOrEqual=" + DEFAULT_STORY_POINTS_COMPLETED,
            "storyPointsCompleted.greaterThanOrEqual=" + UPDATED_STORY_POINTS_COMPLETED
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByStoryPointsCompletedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where storyPointsCompleted is less than or equal to
        defaultIndividualPerformanceFiltering(
            "storyPointsCompleted.lessThanOrEqual=" + DEFAULT_STORY_POINTS_COMPLETED,
            "storyPointsCompleted.lessThanOrEqual=" + SMALLER_STORY_POINTS_COMPLETED
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByStoryPointsCompletedIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where storyPointsCompleted is less than
        defaultIndividualPerformanceFiltering(
            "storyPointsCompleted.lessThan=" + UPDATED_STORY_POINTS_COMPLETED,
            "storyPointsCompleted.lessThan=" + DEFAULT_STORY_POINTS_COMPLETED
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByStoryPointsCompletedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        // Get all the individualPerformanceList where storyPointsCompleted is greater than
        defaultIndividualPerformanceFiltering(
            "storyPointsCompleted.greaterThan=" + SMALLER_STORY_POINTS_COMPLETED,
            "storyPointsCompleted.greaterThan=" + DEFAULT_STORY_POINTS_COMPLETED
        );
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesByPartyIsEqualToSomething() throws Exception {
        Party party;
        if (TestUtil.findAll(em, Party.class).isEmpty()) {
            individualPerformanceRepository.saveAndFlush(individualPerformance);
            party = PartyResourceIT.createEntity();
        } else {
            party = TestUtil.findAll(em, Party.class).get(0);
        }
        em.persist(party);
        em.flush();
        individualPerformance.setParty(party);
        individualPerformanceRepository.saveAndFlush(individualPerformance);
        Long partyId = party.getId();
        // Get all the individualPerformanceList where party equals to partyId
        defaultIndividualPerformanceShouldBeFound("partyId.equals=" + partyId);

        // Get all the individualPerformanceList where party equals to (partyId + 1)
        defaultIndividualPerformanceShouldNotBeFound("partyId.equals=" + (partyId + 1));
    }

    @Test
    @Transactional
    void getAllIndividualPerformancesBySprintIsEqualToSomething() throws Exception {
        Sprint sprint;
        if (TestUtil.findAll(em, Sprint.class).isEmpty()) {
            individualPerformanceRepository.saveAndFlush(individualPerformance);
            sprint = SprintResourceIT.createEntity();
        } else {
            sprint = TestUtil.findAll(em, Sprint.class).get(0);
        }
        em.persist(sprint);
        em.flush();
        individualPerformance.setSprint(sprint);
        individualPerformanceRepository.saveAndFlush(individualPerformance);
        Long sprintId = sprint.getId();
        // Get all the individualPerformanceList where sprint equals to sprintId
        defaultIndividualPerformanceShouldBeFound("sprintId.equals=" + sprintId);

        // Get all the individualPerformanceList where sprint equals to (sprintId + 1)
        defaultIndividualPerformanceShouldNotBeFound("sprintId.equals=" + (sprintId + 1));
    }

    private void defaultIndividualPerformanceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultIndividualPerformanceShouldBeFound(shouldBeFound);
        defaultIndividualPerformanceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultIndividualPerformanceShouldBeFound(String filter) throws Exception {
        restIndividualPerformanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(individualPerformance.getId().intValue())))
            .andExpect(jsonPath("$.[*].completedTasks").value(hasItem(DEFAULT_COMPLETED_TASKS)))
            .andExpect(jsonPath("$.[*].velocity").value(hasItem(DEFAULT_VELOCITY)))
            .andExpect(jsonPath("$.[*].storyPointsCompleted").value(hasItem(DEFAULT_STORY_POINTS_COMPLETED)));

        // Check, that the count call also returns 1
        restIndividualPerformanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultIndividualPerformanceShouldNotBeFound(String filter) throws Exception {
        restIndividualPerformanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restIndividualPerformanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingIndividualPerformance() throws Exception {
        // Get the individualPerformance
        restIndividualPerformanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIndividualPerformance() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the individualPerformance
        IndividualPerformance updatedIndividualPerformance = individualPerformanceRepository
            .findById(individualPerformance.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedIndividualPerformance are not directly saved in db
        em.detach(updatedIndividualPerformance);
        updatedIndividualPerformance
            .completedTasks(UPDATED_COMPLETED_TASKS)
            .velocity(UPDATED_VELOCITY)
            .storyPointsCompleted(UPDATED_STORY_POINTS_COMPLETED);

        restIndividualPerformanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedIndividualPerformance.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedIndividualPerformance))
            )
            .andExpect(status().isOk());

        // Validate the IndividualPerformance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedIndividualPerformanceToMatchAllProperties(updatedIndividualPerformance);
    }

    @Test
    @Transactional
    void putNonExistingIndividualPerformance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        individualPerformance.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIndividualPerformanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, individualPerformance.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(individualPerformance))
            )
            .andExpect(status().isBadRequest());

        // Validate the IndividualPerformance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIndividualPerformance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        individualPerformance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIndividualPerformanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(individualPerformance))
            )
            .andExpect(status().isBadRequest());

        // Validate the IndividualPerformance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIndividualPerformance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        individualPerformance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIndividualPerformanceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(individualPerformance)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the IndividualPerformance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIndividualPerformanceWithPatch() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the individualPerformance using partial update
        IndividualPerformance partialUpdatedIndividualPerformance = new IndividualPerformance();
        partialUpdatedIndividualPerformance.setId(individualPerformance.getId());

        partialUpdatedIndividualPerformance
            .completedTasks(UPDATED_COMPLETED_TASKS)
            .velocity(UPDATED_VELOCITY)
            .storyPointsCompleted(UPDATED_STORY_POINTS_COMPLETED);

        restIndividualPerformanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIndividualPerformance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIndividualPerformance))
            )
            .andExpect(status().isOk());

        // Validate the IndividualPerformance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIndividualPerformanceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedIndividualPerformance, individualPerformance),
            getPersistedIndividualPerformance(individualPerformance)
        );
    }

    @Test
    @Transactional
    void fullUpdateIndividualPerformanceWithPatch() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the individualPerformance using partial update
        IndividualPerformance partialUpdatedIndividualPerformance = new IndividualPerformance();
        partialUpdatedIndividualPerformance.setId(individualPerformance.getId());

        partialUpdatedIndividualPerformance
            .completedTasks(UPDATED_COMPLETED_TASKS)
            .velocity(UPDATED_VELOCITY)
            .storyPointsCompleted(UPDATED_STORY_POINTS_COMPLETED);

        restIndividualPerformanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIndividualPerformance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIndividualPerformance))
            )
            .andExpect(status().isOk());

        // Validate the IndividualPerformance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIndividualPerformanceUpdatableFieldsEquals(
            partialUpdatedIndividualPerformance,
            getPersistedIndividualPerformance(partialUpdatedIndividualPerformance)
        );
    }

    @Test
    @Transactional
    void patchNonExistingIndividualPerformance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        individualPerformance.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIndividualPerformanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, individualPerformance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(individualPerformance))
            )
            .andExpect(status().isBadRequest());

        // Validate the IndividualPerformance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIndividualPerformance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        individualPerformance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIndividualPerformanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(individualPerformance))
            )
            .andExpect(status().isBadRequest());

        // Validate the IndividualPerformance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIndividualPerformance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        individualPerformance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIndividualPerformanceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(individualPerformance)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the IndividualPerformance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIndividualPerformance() throws Exception {
        // Initialize the database
        insertedIndividualPerformance = individualPerformanceRepository.saveAndFlush(individualPerformance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the individualPerformance
        restIndividualPerformanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, individualPerformance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return individualPerformanceRepository.count();
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

    protected IndividualPerformance getPersistedIndividualPerformance(IndividualPerformance individualPerformance) {
        return individualPerformanceRepository.findById(individualPerformance.getId()).orElseThrow();
    }

    protected void assertPersistedIndividualPerformanceToMatchAllProperties(IndividualPerformance expectedIndividualPerformance) {
        assertIndividualPerformanceAllPropertiesEquals(
            expectedIndividualPerformance,
            getPersistedIndividualPerformance(expectedIndividualPerformance)
        );
    }

    protected void assertPersistedIndividualPerformanceToMatchUpdatableProperties(IndividualPerformance expectedIndividualPerformance) {
        assertIndividualPerformanceAllUpdatablePropertiesEquals(
            expectedIndividualPerformance,
            getPersistedIndividualPerformance(expectedIndividualPerformance)
        );
    }
}
