package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.TaskHistoryAsserts.*;
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
import in.beze.pilot.domain.TaskHistory;
import in.beze.pilot.repository.TaskHistoryRepository;
import in.beze.pilot.service.TaskHistoryService;
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
 * Integration tests for the {@link TaskHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TaskHistoryResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/task-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    @Mock
    private TaskHistoryRepository taskHistoryRepositoryMock;

    @Mock
    private TaskHistoryService taskHistoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskHistoryMockMvc;

    private TaskHistory taskHistory;

    private TaskHistory insertedTaskHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskHistory createEntity() {
        return new TaskHistory().type(DEFAULT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskHistory createUpdatedEntity() {
        return new TaskHistory().type(UPDATED_TYPE);
    }

    @BeforeEach
    public void initTest() {
        taskHistory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTaskHistory != null) {
            taskHistoryRepository.delete(insertedTaskHistory);
            insertedTaskHistory = null;
        }
    }

    @Test
    @Transactional
    void createTaskHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TaskHistory
        var returnedTaskHistory = om.readValue(
            restTaskHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskHistory)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TaskHistory.class
        );

        // Validate the TaskHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTaskHistoryUpdatableFieldsEquals(returnedTaskHistory, getPersistedTaskHistory(returnedTaskHistory));

        insertedTaskHistory = returnedTaskHistory;
    }

    @Test
    @Transactional
    void createTaskHistoryWithExistingId() throws Exception {
        // Create the TaskHistory with an existing ID
        taskHistory.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskHistory)))
            .andExpect(status().isBadRequest());

        // Validate the TaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTaskHistories() throws Exception {
        // Initialize the database
        insertedTaskHistory = taskHistoryRepository.saveAndFlush(taskHistory);

        // Get all the taskHistoryList
        restTaskHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTaskHistoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(taskHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTaskHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(taskHistoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTaskHistoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(taskHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTaskHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(taskHistoryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTaskHistory() throws Exception {
        // Initialize the database
        insertedTaskHistory = taskHistoryRepository.saveAndFlush(taskHistory);

        // Get the taskHistory
        restTaskHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, taskHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taskHistory.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
    }

    @Test
    @Transactional
    void getTaskHistoriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedTaskHistory = taskHistoryRepository.saveAndFlush(taskHistory);

        Long id = taskHistory.getId();

        defaultTaskHistoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTaskHistoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTaskHistoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTaskHistoriesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTaskHistory = taskHistoryRepository.saveAndFlush(taskHistory);

        // Get all the taskHistoryList where type equals to
        defaultTaskHistoryFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTaskHistoriesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTaskHistory = taskHistoryRepository.saveAndFlush(taskHistory);

        // Get all the taskHistoryList where type in
        defaultTaskHistoryFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTaskHistoriesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTaskHistory = taskHistoryRepository.saveAndFlush(taskHistory);

        // Get all the taskHistoryList where type is not null
        defaultTaskHistoryFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskHistoriesByTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedTaskHistory = taskHistoryRepository.saveAndFlush(taskHistory);

        // Get all the taskHistoryList where type contains
        defaultTaskHistoryFiltering("type.contains=" + DEFAULT_TYPE, "type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTaskHistoriesByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTaskHistory = taskHistoryRepository.saveAndFlush(taskHistory);

        // Get all the taskHistoryList where type does not contain
        defaultTaskHistoryFiltering("type.doesNotContain=" + UPDATED_TYPE, "type.doesNotContain=" + DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void getAllTaskHistoriesByAssignedToIsEqualToSomething() throws Exception {
        Party assignedTo;
        if (TestUtil.findAll(em, Party.class).isEmpty()) {
            taskHistoryRepository.saveAndFlush(taskHistory);
            assignedTo = PartyResourceIT.createEntity();
        } else {
            assignedTo = TestUtil.findAll(em, Party.class).get(0);
        }
        em.persist(assignedTo);
        em.flush();
        taskHistory.setAssignedTo(assignedTo);
        taskHistoryRepository.saveAndFlush(taskHistory);
        Long assignedToId = assignedTo.getId();
        // Get all the taskHistoryList where assignedTo equals to assignedToId
        defaultTaskHistoryShouldBeFound("assignedToId.equals=" + assignedToId);

        // Get all the taskHistoryList where assignedTo equals to (assignedToId + 1)
        defaultTaskHistoryShouldNotBeFound("assignedToId.equals=" + (assignedToId + 1));
    }

    @Test
    @Transactional
    void getAllTaskHistoriesBySprintIsEqualToSomething() throws Exception {
        Sprint sprint;
        if (TestUtil.findAll(em, Sprint.class).isEmpty()) {
            taskHistoryRepository.saveAndFlush(taskHistory);
            sprint = SprintResourceIT.createEntity();
        } else {
            sprint = TestUtil.findAll(em, Sprint.class).get(0);
        }
        em.persist(sprint);
        em.flush();
        taskHistory.setSprint(sprint);
        taskHistoryRepository.saveAndFlush(taskHistory);
        Long sprintId = sprint.getId();
        // Get all the taskHistoryList where sprint equals to sprintId
        defaultTaskHistoryShouldBeFound("sprintId.equals=" + sprintId);

        // Get all the taskHistoryList where sprint equals to (sprintId + 1)
        defaultTaskHistoryShouldNotBeFound("sprintId.equals=" + (sprintId + 1));
    }

    @Test
    @Transactional
    void getAllTaskHistoriesByAssignedByIsEqualToSomething() throws Exception {
        Party assignedBy;
        if (TestUtil.findAll(em, Party.class).isEmpty()) {
            taskHistoryRepository.saveAndFlush(taskHistory);
            assignedBy = PartyResourceIT.createEntity();
        } else {
            assignedBy = TestUtil.findAll(em, Party.class).get(0);
        }
        em.persist(assignedBy);
        em.flush();
        taskHistory.setAssignedBy(assignedBy);
        taskHistoryRepository.saveAndFlush(taskHistory);
        Long assignedById = assignedBy.getId();
        // Get all the taskHistoryList where assignedBy equals to assignedById
        defaultTaskHistoryShouldBeFound("assignedById.equals=" + assignedById);

        // Get all the taskHistoryList where assignedBy equals to (assignedById + 1)
        defaultTaskHistoryShouldNotBeFound("assignedById.equals=" + (assignedById + 1));
    }

    private void defaultTaskHistoryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTaskHistoryShouldBeFound(shouldBeFound);
        defaultTaskHistoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskHistoryShouldBeFound(String filter) throws Exception {
        restTaskHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));

        // Check, that the count call also returns 1
        restTaskHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskHistoryShouldNotBeFound(String filter) throws Exception {
        restTaskHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTaskHistory() throws Exception {
        // Get the taskHistory
        restTaskHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTaskHistory() throws Exception {
        // Initialize the database
        insertedTaskHistory = taskHistoryRepository.saveAndFlush(taskHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskHistory
        TaskHistory updatedTaskHistory = taskHistoryRepository.findById(taskHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTaskHistory are not directly saved in db
        em.detach(updatedTaskHistory);
        updatedTaskHistory.type(UPDATED_TYPE);

        restTaskHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTaskHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTaskHistory))
            )
            .andExpect(status().isOk());

        // Validate the TaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTaskHistoryToMatchAllProperties(updatedTaskHistory);
    }

    @Test
    @Transactional
    void putNonExistingTaskHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskHistory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaskHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaskHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedTaskHistory = taskHistoryRepository.saveAndFlush(taskHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskHistory using partial update
        TaskHistory partialUpdatedTaskHistory = new TaskHistory();
        partialUpdatedTaskHistory.setId(taskHistory.getId());

        partialUpdatedTaskHistory.type(UPDATED_TYPE);

        restTaskHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskHistory))
            )
            .andExpect(status().isOk());

        // Validate the TaskHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTaskHistory, taskHistory),
            getPersistedTaskHistory(taskHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateTaskHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedTaskHistory = taskHistoryRepository.saveAndFlush(taskHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskHistory using partial update
        TaskHistory partialUpdatedTaskHistory = new TaskHistory();
        partialUpdatedTaskHistory.setId(taskHistory.getId());

        partialUpdatedTaskHistory.type(UPDATED_TYPE);

        restTaskHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskHistory))
            )
            .andExpect(status().isOk());

        // Validate the TaskHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskHistoryUpdatableFieldsEquals(partialUpdatedTaskHistory, getPersistedTaskHistory(partialUpdatedTaskHistory));
    }

    @Test
    @Transactional
    void patchNonExistingTaskHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskHistory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaskHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaskHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(taskHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTaskHistory() throws Exception {
        // Initialize the database
        insertedTaskHistory = taskHistoryRepository.saveAndFlush(taskHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the taskHistory
        restTaskHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, taskHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return taskHistoryRepository.count();
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

    protected TaskHistory getPersistedTaskHistory(TaskHistory taskHistory) {
        return taskHistoryRepository.findById(taskHistory.getId()).orElseThrow();
    }

    protected void assertPersistedTaskHistoryToMatchAllProperties(TaskHistory expectedTaskHistory) {
        assertTaskHistoryAllPropertiesEquals(expectedTaskHistory, getPersistedTaskHistory(expectedTaskHistory));
    }

    protected void assertPersistedTaskHistoryToMatchUpdatableProperties(TaskHistory expectedTaskHistory) {
        assertTaskHistoryAllUpdatablePropertiesEquals(expectedTaskHistory, getPersistedTaskHistory(expectedTaskHistory));
    }
}
