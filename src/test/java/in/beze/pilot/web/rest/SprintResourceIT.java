package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.SprintAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.Project;
import in.beze.pilot.domain.Sprint;
import in.beze.pilot.domain.Status;
import in.beze.pilot.repository.SprintRepository;
import in.beze.pilot.service.SprintService;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link SprintResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SprintResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_GOAL = "AAAAAAAAAA";
    private static final String UPDATED_GOAL = "BBBBBBBBBB";

    private static final Integer DEFAULT_TOTAL_POINTS = 1;
    private static final Integer UPDATED_TOTAL_POINTS = 2;
    private static final Integer SMALLER_TOTAL_POINTS = 1 - 1;

    private static final String ENTITY_API_URL = "/api/sprints";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SprintRepository sprintRepository;

    @Mock
    private SprintRepository sprintRepositoryMock;

    @Mock
    private SprintService sprintServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSprintMockMvc;

    private Sprint sprint;

    private Sprint insertedSprint;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sprint createEntity() {
        return new Sprint()
            .name(DEFAULT_NAME)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .goal(DEFAULT_GOAL)
            .totalPoints(DEFAULT_TOTAL_POINTS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sprint createUpdatedEntity() {
        return new Sprint()
            .name(UPDATED_NAME)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .goal(UPDATED_GOAL)
            .totalPoints(UPDATED_TOTAL_POINTS);
    }

    @BeforeEach
    public void initTest() {
        sprint = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSprint != null) {
            sprintRepository.delete(insertedSprint);
            insertedSprint = null;
        }
    }

    @Test
    @Transactional
    void createSprint() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Sprint
        var returnedSprint = om.readValue(
            restSprintMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprint)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Sprint.class
        );

        // Validate the Sprint in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSprintUpdatableFieldsEquals(returnedSprint, getPersistedSprint(returnedSprint));

        insertedSprint = returnedSprint;
    }

    @Test
    @Transactional
    void createSprintWithExistingId() throws Exception {
        // Create the Sprint with an existing ID
        sprint.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSprintMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprint)))
            .andExpect(status().isBadRequest());

        // Validate the Sprint in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sprint.setName(null);

        // Create the Sprint, which fails.

        restSprintMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprint)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sprint.setStartDate(null);

        // Create the Sprint, which fails.

        restSprintMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprint)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sprint.setEndDate(null);

        // Create the Sprint, which fails.

        restSprintMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprint)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSprints() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList
        restSprintMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sprint.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].goal").value(hasItem(DEFAULT_GOAL)))
            .andExpect(jsonPath("$.[*].totalPoints").value(hasItem(DEFAULT_TOTAL_POINTS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSprintsWithEagerRelationshipsIsEnabled() throws Exception {
        when(sprintServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSprintMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sprintServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSprintsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sprintServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSprintMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sprintRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSprint() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get the sprint
        restSprintMockMvc
            .perform(get(ENTITY_API_URL_ID, sprint.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sprint.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.goal").value(DEFAULT_GOAL))
            .andExpect(jsonPath("$.totalPoints").value(DEFAULT_TOTAL_POINTS));
    }

    @Test
    @Transactional
    void getSprintsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        Long id = sprint.getId();

        defaultSprintFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSprintFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSprintFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSprintsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where name equals to
        defaultSprintFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSprintsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where name in
        defaultSprintFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSprintsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where name is not null
        defaultSprintFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where name contains
        defaultSprintFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSprintsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where name does not contain
        defaultSprintFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllSprintsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where startDate equals to
        defaultSprintFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllSprintsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where startDate in
        defaultSprintFiltering("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE, "startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllSprintsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where startDate is not null
        defaultSprintFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintsByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where startDate is greater than or equal to
        defaultSprintFiltering("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE, "startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllSprintsByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where startDate is less than or equal to
        defaultSprintFiltering("startDate.lessThanOrEqual=" + DEFAULT_START_DATE, "startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllSprintsByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where startDate is less than
        defaultSprintFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void getAllSprintsByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where startDate is greater than
        defaultSprintFiltering("startDate.greaterThan=" + SMALLER_START_DATE, "startDate.greaterThan=" + DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void getAllSprintsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where endDate equals to
        defaultSprintFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllSprintsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where endDate in
        defaultSprintFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllSprintsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where endDate is not null
        defaultSprintFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintsByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where endDate is greater than or equal to
        defaultSprintFiltering("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE, "endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllSprintsByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where endDate is less than or equal to
        defaultSprintFiltering("endDate.lessThanOrEqual=" + DEFAULT_END_DATE, "endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllSprintsByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where endDate is less than
        defaultSprintFiltering("endDate.lessThan=" + UPDATED_END_DATE, "endDate.lessThan=" + DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void getAllSprintsByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where endDate is greater than
        defaultSprintFiltering("endDate.greaterThan=" + SMALLER_END_DATE, "endDate.greaterThan=" + DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void getAllSprintsByGoalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where goal equals to
        defaultSprintFiltering("goal.equals=" + DEFAULT_GOAL, "goal.equals=" + UPDATED_GOAL);
    }

    @Test
    @Transactional
    void getAllSprintsByGoalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where goal in
        defaultSprintFiltering("goal.in=" + DEFAULT_GOAL + "," + UPDATED_GOAL, "goal.in=" + UPDATED_GOAL);
    }

    @Test
    @Transactional
    void getAllSprintsByGoalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where goal is not null
        defaultSprintFiltering("goal.specified=true", "goal.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintsByGoalContainsSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where goal contains
        defaultSprintFiltering("goal.contains=" + DEFAULT_GOAL, "goal.contains=" + UPDATED_GOAL);
    }

    @Test
    @Transactional
    void getAllSprintsByGoalNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where goal does not contain
        defaultSprintFiltering("goal.doesNotContain=" + UPDATED_GOAL, "goal.doesNotContain=" + DEFAULT_GOAL);
    }

    @Test
    @Transactional
    void getAllSprintsByTotalPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where totalPoints equals to
        defaultSprintFiltering("totalPoints.equals=" + DEFAULT_TOTAL_POINTS, "totalPoints.equals=" + UPDATED_TOTAL_POINTS);
    }

    @Test
    @Transactional
    void getAllSprintsByTotalPointsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where totalPoints in
        defaultSprintFiltering(
            "totalPoints.in=" + DEFAULT_TOTAL_POINTS + "," + UPDATED_TOTAL_POINTS,
            "totalPoints.in=" + UPDATED_TOTAL_POINTS
        );
    }

    @Test
    @Transactional
    void getAllSprintsByTotalPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where totalPoints is not null
        defaultSprintFiltering("totalPoints.specified=true", "totalPoints.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintsByTotalPointsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where totalPoints is greater than or equal to
        defaultSprintFiltering(
            "totalPoints.greaterThanOrEqual=" + DEFAULT_TOTAL_POINTS,
            "totalPoints.greaterThanOrEqual=" + UPDATED_TOTAL_POINTS
        );
    }

    @Test
    @Transactional
    void getAllSprintsByTotalPointsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where totalPoints is less than or equal to
        defaultSprintFiltering(
            "totalPoints.lessThanOrEqual=" + DEFAULT_TOTAL_POINTS,
            "totalPoints.lessThanOrEqual=" + SMALLER_TOTAL_POINTS
        );
    }

    @Test
    @Transactional
    void getAllSprintsByTotalPointsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where totalPoints is less than
        defaultSprintFiltering("totalPoints.lessThan=" + UPDATED_TOTAL_POINTS, "totalPoints.lessThan=" + DEFAULT_TOTAL_POINTS);
    }

    @Test
    @Transactional
    void getAllSprintsByTotalPointsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        // Get all the sprintList where totalPoints is greater than
        defaultSprintFiltering("totalPoints.greaterThan=" + SMALLER_TOTAL_POINTS, "totalPoints.greaterThan=" + DEFAULT_TOTAL_POINTS);
    }

    @Test
    @Transactional
    void getAllSprintsByProjectIsEqualToSomething() throws Exception {
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            sprintRepository.saveAndFlush(sprint);
            project = ProjectResourceIT.createEntity();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        em.persist(project);
        em.flush();
        sprint.setProject(project);
        sprintRepository.saveAndFlush(sprint);
        Long projectId = project.getId();
        // Get all the sprintList where project equals to projectId
        defaultSprintShouldBeFound("projectId.equals=" + projectId);

        // Get all the sprintList where project equals to (projectId + 1)
        defaultSprintShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    @Test
    @Transactional
    void getAllSprintsByStatusIsEqualToSomething() throws Exception {
        Status status;
        if (TestUtil.findAll(em, Status.class).isEmpty()) {
            sprintRepository.saveAndFlush(sprint);
            status = StatusResourceIT.createEntity();
        } else {
            status = TestUtil.findAll(em, Status.class).get(0);
        }
        em.persist(status);
        em.flush();
        sprint.setStatus(status);
        sprintRepository.saveAndFlush(sprint);
        Long statusId = status.getId();
        // Get all the sprintList where status equals to statusId
        defaultSprintShouldBeFound("statusId.equals=" + statusId);

        // Get all the sprintList where status equals to (statusId + 1)
        defaultSprintShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    private void defaultSprintFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSprintShouldBeFound(shouldBeFound);
        defaultSprintShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSprintShouldBeFound(String filter) throws Exception {
        restSprintMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sprint.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].goal").value(hasItem(DEFAULT_GOAL)))
            .andExpect(jsonPath("$.[*].totalPoints").value(hasItem(DEFAULT_TOTAL_POINTS)));

        // Check, that the count call also returns 1
        restSprintMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSprintShouldNotBeFound(String filter) throws Exception {
        restSprintMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSprintMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSprint() throws Exception {
        // Get the sprint
        restSprintMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSprint() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sprint
        Sprint updatedSprint = sprintRepository.findById(sprint.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSprint are not directly saved in db
        em.detach(updatedSprint);
        updatedSprint
            .name(UPDATED_NAME)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .goal(UPDATED_GOAL)
            .totalPoints(UPDATED_TOTAL_POINTS);

        restSprintMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSprint.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSprint))
            )
            .andExpect(status().isOk());

        // Validate the Sprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSprintToMatchAllProperties(updatedSprint);
    }

    @Test
    @Transactional
    void putNonExistingSprint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprint.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSprintMockMvc
            .perform(put(ENTITY_API_URL_ID, sprint.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprint)))
            .andExpect(status().isBadRequest());

        // Validate the Sprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSprint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprint.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sprint))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSprint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprint.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprint)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSprintWithPatch() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sprint using partial update
        Sprint partialUpdatedSprint = new Sprint();
        partialUpdatedSprint.setId(sprint.getId());

        partialUpdatedSprint.name(UPDATED_NAME).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restSprintMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSprint.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSprint))
            )
            .andExpect(status().isOk());

        // Validate the Sprint in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSprintUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSprint, sprint), getPersistedSprint(sprint));
    }

    @Test
    @Transactional
    void fullUpdateSprintWithPatch() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sprint using partial update
        Sprint partialUpdatedSprint = new Sprint();
        partialUpdatedSprint.setId(sprint.getId());

        partialUpdatedSprint
            .name(UPDATED_NAME)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .goal(UPDATED_GOAL)
            .totalPoints(UPDATED_TOTAL_POINTS);

        restSprintMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSprint.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSprint))
            )
            .andExpect(status().isOk());

        // Validate the Sprint in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSprintUpdatableFieldsEquals(partialUpdatedSprint, getPersistedSprint(partialUpdatedSprint));
    }

    @Test
    @Transactional
    void patchNonExistingSprint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprint.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSprintMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sprint.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sprint))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSprint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprint.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sprint))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSprint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprint.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sprint)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sprint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSprint() throws Exception {
        // Initialize the database
        insertedSprint = sprintRepository.saveAndFlush(sprint);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sprint
        restSprintMockMvc
            .perform(delete(ENTITY_API_URL_ID, sprint.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sprintRepository.count();
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

    protected Sprint getPersistedSprint(Sprint sprint) {
        return sprintRepository.findById(sprint.getId()).orElseThrow();
    }

    protected void assertPersistedSprintToMatchAllProperties(Sprint expectedSprint) {
        assertSprintAllPropertiesEquals(expectedSprint, getPersistedSprint(expectedSprint));
    }

    protected void assertPersistedSprintToMatchUpdatableProperties(Sprint expectedSprint) {
        assertSprintAllUpdatablePropertiesEquals(expectedSprint, getPersistedSprint(expectedSprint));
    }
}
