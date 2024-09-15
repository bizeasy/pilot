package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.TaskLogAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.Task;
import in.beze.pilot.domain.TaskLog;
import in.beze.pilot.repository.TaskLogRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TaskLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskLogResourceIT {

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final Instant DEFAULT_FROM_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FROM_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_TO_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TO_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/task-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskLogRepository taskLogRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskLogMockMvc;

    private TaskLog taskLog;

    private TaskLog insertedTaskLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskLog createEntity() {
        return new TaskLog().comments(DEFAULT_COMMENTS).fromTime(DEFAULT_FROM_TIME).toTime(DEFAULT_TO_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskLog createUpdatedEntity() {
        return new TaskLog().comments(UPDATED_COMMENTS).fromTime(UPDATED_FROM_TIME).toTime(UPDATED_TO_TIME);
    }

    @BeforeEach
    public void initTest() {
        taskLog = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTaskLog != null) {
            taskLogRepository.delete(insertedTaskLog);
            insertedTaskLog = null;
        }
    }

    @Test
    @Transactional
    void createTaskLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TaskLog
        var returnedTaskLog = om.readValue(
            restTaskLogMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskLog)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TaskLog.class
        );

        // Validate the TaskLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTaskLogUpdatableFieldsEquals(returnedTaskLog, getPersistedTaskLog(returnedTaskLog));

        insertedTaskLog = returnedTaskLog;
    }

    @Test
    @Transactional
    void createTaskLogWithExistingId() throws Exception {
        // Create the TaskLog with an existing ID
        taskLog.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskLog)))
            .andExpect(status().isBadRequest());

        // Validate the TaskLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTaskLogs() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get all the taskLogList
        restTaskLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)))
            .andExpect(jsonPath("$.[*].fromTime").value(hasItem(DEFAULT_FROM_TIME.toString())))
            .andExpect(jsonPath("$.[*].toTime").value(hasItem(DEFAULT_TO_TIME.toString())));
    }

    @Test
    @Transactional
    void getTaskLog() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get the taskLog
        restTaskLogMockMvc
            .perform(get(ENTITY_API_URL_ID, taskLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taskLog.getId().intValue()))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS))
            .andExpect(jsonPath("$.fromTime").value(DEFAULT_FROM_TIME.toString()))
            .andExpect(jsonPath("$.toTime").value(DEFAULT_TO_TIME.toString()));
    }

    @Test
    @Transactional
    void getTaskLogsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        Long id = taskLog.getId();

        defaultTaskLogFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTaskLogFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTaskLogFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTaskLogsByCommentsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get all the taskLogList where comments equals to
        defaultTaskLogFiltering("comments.equals=" + DEFAULT_COMMENTS, "comments.equals=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void getAllTaskLogsByCommentsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get all the taskLogList where comments in
        defaultTaskLogFiltering("comments.in=" + DEFAULT_COMMENTS + "," + UPDATED_COMMENTS, "comments.in=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void getAllTaskLogsByCommentsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get all the taskLogList where comments is not null
        defaultTaskLogFiltering("comments.specified=true", "comments.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskLogsByCommentsContainsSomething() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get all the taskLogList where comments contains
        defaultTaskLogFiltering("comments.contains=" + DEFAULT_COMMENTS, "comments.contains=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void getAllTaskLogsByCommentsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get all the taskLogList where comments does not contain
        defaultTaskLogFiltering("comments.doesNotContain=" + UPDATED_COMMENTS, "comments.doesNotContain=" + DEFAULT_COMMENTS);
    }

    @Test
    @Transactional
    void getAllTaskLogsByFromTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get all the taskLogList where fromTime equals to
        defaultTaskLogFiltering("fromTime.equals=" + DEFAULT_FROM_TIME, "fromTime.equals=" + UPDATED_FROM_TIME);
    }

    @Test
    @Transactional
    void getAllTaskLogsByFromTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get all the taskLogList where fromTime in
        defaultTaskLogFiltering("fromTime.in=" + DEFAULT_FROM_TIME + "," + UPDATED_FROM_TIME, "fromTime.in=" + UPDATED_FROM_TIME);
    }

    @Test
    @Transactional
    void getAllTaskLogsByFromTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get all the taskLogList where fromTime is not null
        defaultTaskLogFiltering("fromTime.specified=true", "fromTime.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskLogsByToTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get all the taskLogList where toTime equals to
        defaultTaskLogFiltering("toTime.equals=" + DEFAULT_TO_TIME, "toTime.equals=" + UPDATED_TO_TIME);
    }

    @Test
    @Transactional
    void getAllTaskLogsByToTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get all the taskLogList where toTime in
        defaultTaskLogFiltering("toTime.in=" + DEFAULT_TO_TIME + "," + UPDATED_TO_TIME, "toTime.in=" + UPDATED_TO_TIME);
    }

    @Test
    @Transactional
    void getAllTaskLogsByToTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        // Get all the taskLogList where toTime is not null
        defaultTaskLogFiltering("toTime.specified=true", "toTime.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskLogsByTaskIsEqualToSomething() throws Exception {
        Task task;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            taskLogRepository.saveAndFlush(taskLog);
            task = TaskResourceIT.createEntity(em);
        } else {
            task = TestUtil.findAll(em, Task.class).get(0);
        }
        em.persist(task);
        em.flush();
        taskLog.setTask(task);
        taskLogRepository.saveAndFlush(taskLog);
        Long taskId = task.getId();
        // Get all the taskLogList where task equals to taskId
        defaultTaskLogShouldBeFound("taskId.equals=" + taskId);

        // Get all the taskLogList where task equals to (taskId + 1)
        defaultTaskLogShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    private void defaultTaskLogFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTaskLogShouldBeFound(shouldBeFound);
        defaultTaskLogShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskLogShouldBeFound(String filter) throws Exception {
        restTaskLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)))
            .andExpect(jsonPath("$.[*].fromTime").value(hasItem(DEFAULT_FROM_TIME.toString())))
            .andExpect(jsonPath("$.[*].toTime").value(hasItem(DEFAULT_TO_TIME.toString())));

        // Check, that the count call also returns 1
        restTaskLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskLogShouldNotBeFound(String filter) throws Exception {
        restTaskLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTaskLog() throws Exception {
        // Get the taskLog
        restTaskLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTaskLog() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskLog
        TaskLog updatedTaskLog = taskLogRepository.findById(taskLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTaskLog are not directly saved in db
        em.detach(updatedTaskLog);
        updatedTaskLog.comments(UPDATED_COMMENTS).fromTime(UPDATED_FROM_TIME).toTime(UPDATED_TO_TIME);

        restTaskLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTaskLog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTaskLog))
            )
            .andExpect(status().isOk());

        // Validate the TaskLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTaskLogToMatchAllProperties(updatedTaskLog);
    }

    @Test
    @Transactional
    void putNonExistingTaskLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskLog.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskLogMockMvc
            .perform(put(ENTITY_API_URL_ID, taskLog.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskLog)))
            .andExpect(status().isBadRequest());

        // Validate the TaskLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaskLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskLog.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaskLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskLog.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskLog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskLogWithPatch() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskLog using partial update
        TaskLog partialUpdatedTaskLog = new TaskLog();
        partialUpdatedTaskLog.setId(taskLog.getId());

        partialUpdatedTaskLog.fromTime(UPDATED_FROM_TIME).toTime(UPDATED_TO_TIME);

        restTaskLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskLog))
            )
            .andExpect(status().isOk());

        // Validate the TaskLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskLogUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTaskLog, taskLog), getPersistedTaskLog(taskLog));
    }

    @Test
    @Transactional
    void fullUpdateTaskLogWithPatch() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskLog using partial update
        TaskLog partialUpdatedTaskLog = new TaskLog();
        partialUpdatedTaskLog.setId(taskLog.getId());

        partialUpdatedTaskLog.comments(UPDATED_COMMENTS).fromTime(UPDATED_FROM_TIME).toTime(UPDATED_TO_TIME);

        restTaskLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskLog))
            )
            .andExpect(status().isOk());

        // Validate the TaskLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskLogUpdatableFieldsEquals(partialUpdatedTaskLog, getPersistedTaskLog(partialUpdatedTaskLog));
    }

    @Test
    @Transactional
    void patchNonExistingTaskLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskLog.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskLog.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(taskLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaskLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskLog.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaskLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskLog.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(taskLog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTaskLog() throws Exception {
        // Initialize the database
        insertedTaskLog = taskLogRepository.saveAndFlush(taskLog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the taskLog
        restTaskLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, taskLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return taskLogRepository.count();
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

    protected TaskLog getPersistedTaskLog(TaskLog taskLog) {
        return taskLogRepository.findById(taskLog.getId()).orElseThrow();
    }

    protected void assertPersistedTaskLogToMatchAllProperties(TaskLog expectedTaskLog) {
        assertTaskLogAllPropertiesEquals(expectedTaskLog, getPersistedTaskLog(expectedTaskLog));
    }

    protected void assertPersistedTaskLogToMatchUpdatableProperties(TaskLog expectedTaskLog) {
        assertTaskLogAllUpdatablePropertiesEquals(expectedTaskLog, getPersistedTaskLog(expectedTaskLog));
    }
}
