package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.SprintTaskAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.Party;
import in.beze.pilot.domain.Sprint;
import in.beze.pilot.domain.SprintTask;
import in.beze.pilot.domain.Status;
import in.beze.pilot.domain.Task;
import in.beze.pilot.repository.SprintTaskRepository;
import in.beze.pilot.service.SprintTaskService;
import jakarta.persistence.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link SprintTaskResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SprintTaskResourceIT {

    private static final Integer DEFAULT_SEQUENCE_NO = 1;
    private static final Integer UPDATED_SEQUENCE_NO = 2;
    private static final Integer SMALLER_SEQUENCE_NO = 1 - 1;

    private static final Integer DEFAULT_STORY_POINTS = 1;
    private static final Integer UPDATED_STORY_POINTS = 2;
    private static final Integer SMALLER_STORY_POINTS = 1 - 1;

    private static final Instant DEFAULT_FROM_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FROM_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_THRU_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_THRU_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ASSIGNED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSIGNED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Duration DEFAULT_DURATION = Duration.ofHours(6);
    private static final Duration UPDATED_DURATION = Duration.ofHours(12);
    private static final Duration SMALLER_DURATION = Duration.ofHours(5);

    private static final String ENTITY_API_URL = "/api/sprint-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SprintTaskRepository sprintTaskRepository;

    @Mock
    private SprintTaskRepository sprintTaskRepositoryMock;

    @Mock
    private SprintTaskService sprintTaskServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSprintTaskMockMvc;

    private SprintTask sprintTask;

    private SprintTask insertedSprintTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SprintTask createEntity() {
        return new SprintTask()
            .sequenceNo(DEFAULT_SEQUENCE_NO)
            .storyPoints(DEFAULT_STORY_POINTS)
            .fromTime(DEFAULT_FROM_TIME)
            .thruTime(DEFAULT_THRU_TIME)
            .assignedTime(DEFAULT_ASSIGNED_TIME)
            .duration(DEFAULT_DURATION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SprintTask createUpdatedEntity() {
        return new SprintTask()
            .sequenceNo(UPDATED_SEQUENCE_NO)
            .storyPoints(UPDATED_STORY_POINTS)
            .fromTime(UPDATED_FROM_TIME)
            .thruTime(UPDATED_THRU_TIME)
            .assignedTime(UPDATED_ASSIGNED_TIME)
            .duration(UPDATED_DURATION);
    }

    @BeforeEach
    public void initTest() {
        sprintTask = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSprintTask != null) {
            sprintTaskRepository.delete(insertedSprintTask);
            insertedSprintTask = null;
        }
    }

    @Test
    @Transactional
    void createSprintTask() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SprintTask
        var returnedSprintTask = om.readValue(
            restSprintTaskMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprintTask)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SprintTask.class
        );

        // Validate the SprintTask in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSprintTaskUpdatableFieldsEquals(returnedSprintTask, getPersistedSprintTask(returnedSprintTask));

        insertedSprintTask = returnedSprintTask;
    }

    @Test
    @Transactional
    void createSprintTaskWithExistingId() throws Exception {
        // Create the SprintTask with an existing ID
        sprintTask.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSprintTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprintTask)))
            .andExpect(status().isBadRequest());

        // Validate the SprintTask in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSprintTasks() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList
        restSprintTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sprintTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].sequenceNo").value(hasItem(DEFAULT_SEQUENCE_NO)))
            .andExpect(jsonPath("$.[*].storyPoints").value(hasItem(DEFAULT_STORY_POINTS)))
            .andExpect(jsonPath("$.[*].fromTime").value(hasItem(DEFAULT_FROM_TIME.toString())))
            .andExpect(jsonPath("$.[*].thruTime").value(hasItem(DEFAULT_THRU_TIME.toString())))
            .andExpect(jsonPath("$.[*].assignedTime").value(hasItem(DEFAULT_ASSIGNED_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSprintTasksWithEagerRelationshipsIsEnabled() throws Exception {
        when(sprintTaskServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSprintTaskMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sprintTaskServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSprintTasksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sprintTaskServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSprintTaskMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sprintTaskRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSprintTask() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get the sprintTask
        restSprintTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, sprintTask.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sprintTask.getId().intValue()))
            .andExpect(jsonPath("$.sequenceNo").value(DEFAULT_SEQUENCE_NO))
            .andExpect(jsonPath("$.storyPoints").value(DEFAULT_STORY_POINTS))
            .andExpect(jsonPath("$.fromTime").value(DEFAULT_FROM_TIME.toString()))
            .andExpect(jsonPath("$.thruTime").value(DEFAULT_THRU_TIME.toString()))
            .andExpect(jsonPath("$.assignedTime").value(DEFAULT_ASSIGNED_TIME.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.toString()));
    }

    @Test
    @Transactional
    void getSprintTasksByIdFiltering() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        Long id = sprintTask.getId();

        defaultSprintTaskFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSprintTaskFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSprintTaskFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSprintTasksBySequenceNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where sequenceNo equals to
        defaultSprintTaskFiltering("sequenceNo.equals=" + DEFAULT_SEQUENCE_NO, "sequenceNo.equals=" + UPDATED_SEQUENCE_NO);
    }

    @Test
    @Transactional
    void getAllSprintTasksBySequenceNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where sequenceNo in
        defaultSprintTaskFiltering(
            "sequenceNo.in=" + DEFAULT_SEQUENCE_NO + "," + UPDATED_SEQUENCE_NO,
            "sequenceNo.in=" + UPDATED_SEQUENCE_NO
        );
    }

    @Test
    @Transactional
    void getAllSprintTasksBySequenceNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where sequenceNo is not null
        defaultSprintTaskFiltering("sequenceNo.specified=true", "sequenceNo.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintTasksBySequenceNoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where sequenceNo is greater than or equal to
        defaultSprintTaskFiltering(
            "sequenceNo.greaterThanOrEqual=" + DEFAULT_SEQUENCE_NO,
            "sequenceNo.greaterThanOrEqual=" + UPDATED_SEQUENCE_NO
        );
    }

    @Test
    @Transactional
    void getAllSprintTasksBySequenceNoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where sequenceNo is less than or equal to
        defaultSprintTaskFiltering(
            "sequenceNo.lessThanOrEqual=" + DEFAULT_SEQUENCE_NO,
            "sequenceNo.lessThanOrEqual=" + SMALLER_SEQUENCE_NO
        );
    }

    @Test
    @Transactional
    void getAllSprintTasksBySequenceNoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where sequenceNo is less than
        defaultSprintTaskFiltering("sequenceNo.lessThan=" + UPDATED_SEQUENCE_NO, "sequenceNo.lessThan=" + DEFAULT_SEQUENCE_NO);
    }

    @Test
    @Transactional
    void getAllSprintTasksBySequenceNoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where sequenceNo is greater than
        defaultSprintTaskFiltering("sequenceNo.greaterThan=" + SMALLER_SEQUENCE_NO, "sequenceNo.greaterThan=" + DEFAULT_SEQUENCE_NO);
    }

    @Test
    @Transactional
    void getAllSprintTasksByStoryPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where storyPoints equals to
        defaultSprintTaskFiltering("storyPoints.equals=" + DEFAULT_STORY_POINTS, "storyPoints.equals=" + UPDATED_STORY_POINTS);
    }

    @Test
    @Transactional
    void getAllSprintTasksByStoryPointsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where storyPoints in
        defaultSprintTaskFiltering(
            "storyPoints.in=" + DEFAULT_STORY_POINTS + "," + UPDATED_STORY_POINTS,
            "storyPoints.in=" + UPDATED_STORY_POINTS
        );
    }

    @Test
    @Transactional
    void getAllSprintTasksByStoryPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where storyPoints is not null
        defaultSprintTaskFiltering("storyPoints.specified=true", "storyPoints.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintTasksByStoryPointsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where storyPoints is greater than or equal to
        defaultSprintTaskFiltering(
            "storyPoints.greaterThanOrEqual=" + DEFAULT_STORY_POINTS,
            "storyPoints.greaterThanOrEqual=" + UPDATED_STORY_POINTS
        );
    }

    @Test
    @Transactional
    void getAllSprintTasksByStoryPointsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where storyPoints is less than or equal to
        defaultSprintTaskFiltering(
            "storyPoints.lessThanOrEqual=" + DEFAULT_STORY_POINTS,
            "storyPoints.lessThanOrEqual=" + SMALLER_STORY_POINTS
        );
    }

    @Test
    @Transactional
    void getAllSprintTasksByStoryPointsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where storyPoints is less than
        defaultSprintTaskFiltering("storyPoints.lessThan=" + UPDATED_STORY_POINTS, "storyPoints.lessThan=" + DEFAULT_STORY_POINTS);
    }

    @Test
    @Transactional
    void getAllSprintTasksByStoryPointsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where storyPoints is greater than
        defaultSprintTaskFiltering("storyPoints.greaterThan=" + SMALLER_STORY_POINTS, "storyPoints.greaterThan=" + DEFAULT_STORY_POINTS);
    }

    @Test
    @Transactional
    void getAllSprintTasksByFromTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where fromTime equals to
        defaultSprintTaskFiltering("fromTime.equals=" + DEFAULT_FROM_TIME, "fromTime.equals=" + UPDATED_FROM_TIME);
    }

    @Test
    @Transactional
    void getAllSprintTasksByFromTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where fromTime in
        defaultSprintTaskFiltering("fromTime.in=" + DEFAULT_FROM_TIME + "," + UPDATED_FROM_TIME, "fromTime.in=" + UPDATED_FROM_TIME);
    }

    @Test
    @Transactional
    void getAllSprintTasksByFromTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where fromTime is not null
        defaultSprintTaskFiltering("fromTime.specified=true", "fromTime.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintTasksByThruTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where thruTime equals to
        defaultSprintTaskFiltering("thruTime.equals=" + DEFAULT_THRU_TIME, "thruTime.equals=" + UPDATED_THRU_TIME);
    }

    @Test
    @Transactional
    void getAllSprintTasksByThruTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where thruTime in
        defaultSprintTaskFiltering("thruTime.in=" + DEFAULT_THRU_TIME + "," + UPDATED_THRU_TIME, "thruTime.in=" + UPDATED_THRU_TIME);
    }

    @Test
    @Transactional
    void getAllSprintTasksByThruTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where thruTime is not null
        defaultSprintTaskFiltering("thruTime.specified=true", "thruTime.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintTasksByAssignedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where assignedTime equals to
        defaultSprintTaskFiltering("assignedTime.equals=" + DEFAULT_ASSIGNED_TIME, "assignedTime.equals=" + UPDATED_ASSIGNED_TIME);
    }

    @Test
    @Transactional
    void getAllSprintTasksByAssignedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where assignedTime in
        defaultSprintTaskFiltering(
            "assignedTime.in=" + DEFAULT_ASSIGNED_TIME + "," + UPDATED_ASSIGNED_TIME,
            "assignedTime.in=" + UPDATED_ASSIGNED_TIME
        );
    }

    @Test
    @Transactional
    void getAllSprintTasksByAssignedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where assignedTime is not null
        defaultSprintTaskFiltering("assignedTime.specified=true", "assignedTime.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintTasksByDurationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where duration equals to
        defaultSprintTaskFiltering("duration.equals=" + DEFAULT_DURATION, "duration.equals=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllSprintTasksByDurationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where duration in
        defaultSprintTaskFiltering("duration.in=" + DEFAULT_DURATION + "," + UPDATED_DURATION, "duration.in=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllSprintTasksByDurationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where duration is not null
        defaultSprintTaskFiltering("duration.specified=true", "duration.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintTasksByDurationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where duration is greater than or equal to
        defaultSprintTaskFiltering("duration.greaterThanOrEqual=" + DEFAULT_DURATION, "duration.greaterThanOrEqual=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllSprintTasksByDurationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where duration is less than or equal to
        defaultSprintTaskFiltering("duration.lessThanOrEqual=" + DEFAULT_DURATION, "duration.lessThanOrEqual=" + SMALLER_DURATION);
    }

    @Test
    @Transactional
    void getAllSprintTasksByDurationIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where duration is less than
        defaultSprintTaskFiltering("duration.lessThan=" + UPDATED_DURATION, "duration.lessThan=" + DEFAULT_DURATION);
    }

    @Test
    @Transactional
    void getAllSprintTasksByDurationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        // Get all the sprintTaskList where duration is greater than
        defaultSprintTaskFiltering("duration.greaterThan=" + SMALLER_DURATION, "duration.greaterThan=" + DEFAULT_DURATION);
    }

    @Test
    @Transactional
    void getAllSprintTasksByTaskIsEqualToSomething() throws Exception {
        Task task;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            sprintTaskRepository.saveAndFlush(sprintTask);
            task = TaskResourceIT.createEntity(em);
        } else {
            task = TestUtil.findAll(em, Task.class).get(0);
        }
        em.persist(task);
        em.flush();
        sprintTask.setTask(task);
        sprintTaskRepository.saveAndFlush(sprintTask);
        Long taskId = task.getId();
        // Get all the sprintTaskList where task equals to taskId
        defaultSprintTaskShouldBeFound("taskId.equals=" + taskId);

        // Get all the sprintTaskList where task equals to (taskId + 1)
        defaultSprintTaskShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    @Test
    @Transactional
    void getAllSprintTasksBySprintIsEqualToSomething() throws Exception {
        Sprint sprint;
        if (TestUtil.findAll(em, Sprint.class).isEmpty()) {
            sprintTaskRepository.saveAndFlush(sprintTask);
            sprint = SprintResourceIT.createEntity();
        } else {
            sprint = TestUtil.findAll(em, Sprint.class).get(0);
        }
        em.persist(sprint);
        em.flush();
        sprintTask.setSprint(sprint);
        sprintTaskRepository.saveAndFlush(sprintTask);
        Long sprintId = sprint.getId();
        // Get all the sprintTaskList where sprint equals to sprintId
        defaultSprintTaskShouldBeFound("sprintId.equals=" + sprintId);

        // Get all the sprintTaskList where sprint equals to (sprintId + 1)
        defaultSprintTaskShouldNotBeFound("sprintId.equals=" + (sprintId + 1));
    }

    @Test
    @Transactional
    void getAllSprintTasksByAssignedToIsEqualToSomething() throws Exception {
        Party assignedTo;
        if (TestUtil.findAll(em, Party.class).isEmpty()) {
            sprintTaskRepository.saveAndFlush(sprintTask);
            assignedTo = PartyResourceIT.createEntity();
        } else {
            assignedTo = TestUtil.findAll(em, Party.class).get(0);
        }
        em.persist(assignedTo);
        em.flush();
        sprintTask.setAssignedTo(assignedTo);
        sprintTaskRepository.saveAndFlush(sprintTask);
        Long assignedToId = assignedTo.getId();
        // Get all the sprintTaskList where assignedTo equals to assignedToId
        defaultSprintTaskShouldBeFound("assignedToId.equals=" + assignedToId);

        // Get all the sprintTaskList where assignedTo equals to (assignedToId + 1)
        defaultSprintTaskShouldNotBeFound("assignedToId.equals=" + (assignedToId + 1));
    }

    @Test
    @Transactional
    void getAllSprintTasksByAssignedByIsEqualToSomething() throws Exception {
        Party assignedBy;
        if (TestUtil.findAll(em, Party.class).isEmpty()) {
            sprintTaskRepository.saveAndFlush(sprintTask);
            assignedBy = PartyResourceIT.createEntity();
        } else {
            assignedBy = TestUtil.findAll(em, Party.class).get(0);
        }
        em.persist(assignedBy);
        em.flush();
        sprintTask.setAssignedBy(assignedBy);
        sprintTaskRepository.saveAndFlush(sprintTask);
        Long assignedById = assignedBy.getId();
        // Get all the sprintTaskList where assignedBy equals to assignedById
        defaultSprintTaskShouldBeFound("assignedById.equals=" + assignedById);

        // Get all the sprintTaskList where assignedBy equals to (assignedById + 1)
        defaultSprintTaskShouldNotBeFound("assignedById.equals=" + (assignedById + 1));
    }

    @Test
    @Transactional
    void getAllSprintTasksByQaIsEqualToSomething() throws Exception {
        Party qa;
        if (TestUtil.findAll(em, Party.class).isEmpty()) {
            sprintTaskRepository.saveAndFlush(sprintTask);
            qa = PartyResourceIT.createEntity();
        } else {
            qa = TestUtil.findAll(em, Party.class).get(0);
        }
        em.persist(qa);
        em.flush();
        sprintTask.setQa(qa);
        sprintTaskRepository.saveAndFlush(sprintTask);
        Long qaId = qa.getId();
        // Get all the sprintTaskList where qa equals to qaId
        defaultSprintTaskShouldBeFound("qaId.equals=" + qaId);

        // Get all the sprintTaskList where qa equals to (qaId + 1)
        defaultSprintTaskShouldNotBeFound("qaId.equals=" + (qaId + 1));
    }

    @Test
    @Transactional
    void getAllSprintTasksByReviewedByIsEqualToSomething() throws Exception {
        Party reviewedBy;
        if (TestUtil.findAll(em, Party.class).isEmpty()) {
            sprintTaskRepository.saveAndFlush(sprintTask);
            reviewedBy = PartyResourceIT.createEntity();
        } else {
            reviewedBy = TestUtil.findAll(em, Party.class).get(0);
        }
        em.persist(reviewedBy);
        em.flush();
        sprintTask.setReviewedBy(reviewedBy);
        sprintTaskRepository.saveAndFlush(sprintTask);
        Long reviewedById = reviewedBy.getId();
        // Get all the sprintTaskList where reviewedBy equals to reviewedById
        defaultSprintTaskShouldBeFound("reviewedById.equals=" + reviewedById);

        // Get all the sprintTaskList where reviewedBy equals to (reviewedById + 1)
        defaultSprintTaskShouldNotBeFound("reviewedById.equals=" + (reviewedById + 1));
    }

    @Test
    @Transactional
    void getAllSprintTasksByStatusIsEqualToSomething() throws Exception {
        Status status;
        if (TestUtil.findAll(em, Status.class).isEmpty()) {
            sprintTaskRepository.saveAndFlush(sprintTask);
            status = StatusResourceIT.createEntity();
        } else {
            status = TestUtil.findAll(em, Status.class).get(0);
        }
        em.persist(status);
        em.flush();
        sprintTask.setStatus(status);
        sprintTaskRepository.saveAndFlush(sprintTask);
        Long statusId = status.getId();
        // Get all the sprintTaskList where status equals to statusId
        defaultSprintTaskShouldBeFound("statusId.equals=" + statusId);

        // Get all the sprintTaskList where status equals to (statusId + 1)
        defaultSprintTaskShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    private void defaultSprintTaskFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSprintTaskShouldBeFound(shouldBeFound);
        defaultSprintTaskShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSprintTaskShouldBeFound(String filter) throws Exception {
        restSprintTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sprintTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].sequenceNo").value(hasItem(DEFAULT_SEQUENCE_NO)))
            .andExpect(jsonPath("$.[*].storyPoints").value(hasItem(DEFAULT_STORY_POINTS)))
            .andExpect(jsonPath("$.[*].fromTime").value(hasItem(DEFAULT_FROM_TIME.toString())))
            .andExpect(jsonPath("$.[*].thruTime").value(hasItem(DEFAULT_THRU_TIME.toString())))
            .andExpect(jsonPath("$.[*].assignedTime").value(hasItem(DEFAULT_ASSIGNED_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.toString())));

        // Check, that the count call also returns 1
        restSprintTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSprintTaskShouldNotBeFound(String filter) throws Exception {
        restSprintTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSprintTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSprintTask() throws Exception {
        // Get the sprintTask
        restSprintTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSprintTask() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sprintTask
        SprintTask updatedSprintTask = sprintTaskRepository.findById(sprintTask.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSprintTask are not directly saved in db
        em.detach(updatedSprintTask);
        updatedSprintTask
            .sequenceNo(UPDATED_SEQUENCE_NO)
            .storyPoints(UPDATED_STORY_POINTS)
            .fromTime(UPDATED_FROM_TIME)
            .thruTime(UPDATED_THRU_TIME)
            .assignedTime(UPDATED_ASSIGNED_TIME)
            .duration(UPDATED_DURATION);

        restSprintTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSprintTask.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSprintTask))
            )
            .andExpect(status().isOk());

        // Validate the SprintTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSprintTaskToMatchAllProperties(updatedSprintTask);
    }

    @Test
    @Transactional
    void putNonExistingSprintTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintTask.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSprintTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sprintTask.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprintTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the SprintTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSprintTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintTask.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sprintTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the SprintTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSprintTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintTask.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintTaskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprintTask)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SprintTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSprintTaskWithPatch() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sprintTask using partial update
        SprintTask partialUpdatedSprintTask = new SprintTask();
        partialUpdatedSprintTask.setId(sprintTask.getId());

        partialUpdatedSprintTask.storyPoints(UPDATED_STORY_POINTS);

        restSprintTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSprintTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSprintTask))
            )
            .andExpect(status().isOk());

        // Validate the SprintTask in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSprintTaskUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSprintTask, sprintTask),
            getPersistedSprintTask(sprintTask)
        );
    }

    @Test
    @Transactional
    void fullUpdateSprintTaskWithPatch() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sprintTask using partial update
        SprintTask partialUpdatedSprintTask = new SprintTask();
        partialUpdatedSprintTask.setId(sprintTask.getId());

        partialUpdatedSprintTask
            .sequenceNo(UPDATED_SEQUENCE_NO)
            .storyPoints(UPDATED_STORY_POINTS)
            .fromTime(UPDATED_FROM_TIME)
            .thruTime(UPDATED_THRU_TIME)
            .assignedTime(UPDATED_ASSIGNED_TIME)
            .duration(UPDATED_DURATION);

        restSprintTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSprintTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSprintTask))
            )
            .andExpect(status().isOk());

        // Validate the SprintTask in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSprintTaskUpdatableFieldsEquals(partialUpdatedSprintTask, getPersistedSprintTask(partialUpdatedSprintTask));
    }

    @Test
    @Transactional
    void patchNonExistingSprintTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintTask.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSprintTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sprintTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sprintTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the SprintTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSprintTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintTask.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sprintTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the SprintTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSprintTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintTask.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintTaskMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sprintTask)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SprintTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSprintTask() throws Exception {
        // Initialize the database
        insertedSprintTask = sprintTaskRepository.saveAndFlush(sprintTask);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sprintTask
        restSprintTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, sprintTask.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sprintTaskRepository.count();
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

    protected SprintTask getPersistedSprintTask(SprintTask sprintTask) {
        return sprintTaskRepository.findById(sprintTask.getId()).orElseThrow();
    }

    protected void assertPersistedSprintTaskToMatchAllProperties(SprintTask expectedSprintTask) {
        assertSprintTaskAllPropertiesEquals(expectedSprintTask, getPersistedSprintTask(expectedSprintTask));
    }

    protected void assertPersistedSprintTaskToMatchUpdatableProperties(SprintTask expectedSprintTask) {
        assertSprintTaskAllUpdatablePropertiesEquals(expectedSprintTask, getPersistedSprintTask(expectedSprintTask));
    }
}
