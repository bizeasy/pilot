package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.TaskAttachmentAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.Attachment;
import in.beze.pilot.domain.Task;
import in.beze.pilot.domain.TaskAttachment;
import in.beze.pilot.repository.TaskAttachmentRepository;
import in.beze.pilot.service.TaskAttachmentService;
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
 * Integration tests for the {@link TaskAttachmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TaskAttachmentResourceIT {

    private static final String ENTITY_API_URL = "/api/task-attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskAttachmentRepository taskAttachmentRepository;

    @Mock
    private TaskAttachmentRepository taskAttachmentRepositoryMock;

    @Mock
    private TaskAttachmentService taskAttachmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskAttachmentMockMvc;

    private TaskAttachment taskAttachment;

    private TaskAttachment insertedTaskAttachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskAttachment createEntity() {
        return new TaskAttachment();
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskAttachment createUpdatedEntity() {
        return new TaskAttachment();
    }

    @BeforeEach
    public void initTest() {
        taskAttachment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTaskAttachment != null) {
            taskAttachmentRepository.delete(insertedTaskAttachment);
            insertedTaskAttachment = null;
        }
    }

    @Test
    @Transactional
    void createTaskAttachment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TaskAttachment
        var returnedTaskAttachment = om.readValue(
            restTaskAttachmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskAttachment)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TaskAttachment.class
        );

        // Validate the TaskAttachment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTaskAttachmentUpdatableFieldsEquals(returnedTaskAttachment, getPersistedTaskAttachment(returnedTaskAttachment));

        insertedTaskAttachment = returnedTaskAttachment;
    }

    @Test
    @Transactional
    void createTaskAttachmentWithExistingId() throws Exception {
        // Create the TaskAttachment with an existing ID
        taskAttachment.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskAttachment)))
            .andExpect(status().isBadRequest());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTaskAttachments() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        // Get all the taskAttachmentList
        restTaskAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskAttachment.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTaskAttachmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(taskAttachmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTaskAttachmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(taskAttachmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTaskAttachmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(taskAttachmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTaskAttachmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(taskAttachmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTaskAttachment() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        // Get the taskAttachment
        restTaskAttachmentMockMvc
            .perform(get(ENTITY_API_URL_ID, taskAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taskAttachment.getId().intValue()));
    }

    @Test
    @Transactional
    void getTaskAttachmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        Long id = taskAttachment.getId();

        defaultTaskAttachmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTaskAttachmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTaskAttachmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTaskAttachmentsByTaskIsEqualToSomething() throws Exception {
        Task task;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            taskAttachmentRepository.saveAndFlush(taskAttachment);
            task = TaskResourceIT.createEntity(em);
        } else {
            task = TestUtil.findAll(em, Task.class).get(0);
        }
        em.persist(task);
        em.flush();
        taskAttachment.setTask(task);
        taskAttachmentRepository.saveAndFlush(taskAttachment);
        Long taskId = task.getId();
        // Get all the taskAttachmentList where task equals to taskId
        defaultTaskAttachmentShouldBeFound("taskId.equals=" + taskId);

        // Get all the taskAttachmentList where task equals to (taskId + 1)
        defaultTaskAttachmentShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    @Test
    @Transactional
    void getAllTaskAttachmentsByAttachmentIsEqualToSomething() throws Exception {
        Attachment attachment;
        if (TestUtil.findAll(em, Attachment.class).isEmpty()) {
            taskAttachmentRepository.saveAndFlush(taskAttachment);
            attachment = AttachmentResourceIT.createEntity();
        } else {
            attachment = TestUtil.findAll(em, Attachment.class).get(0);
        }
        em.persist(attachment);
        em.flush();
        taskAttachment.setAttachment(attachment);
        taskAttachmentRepository.saveAndFlush(taskAttachment);
        Long attachmentId = attachment.getId();
        // Get all the taskAttachmentList where attachment equals to attachmentId
        defaultTaskAttachmentShouldBeFound("attachmentId.equals=" + attachmentId);

        // Get all the taskAttachmentList where attachment equals to (attachmentId + 1)
        defaultTaskAttachmentShouldNotBeFound("attachmentId.equals=" + (attachmentId + 1));
    }

    private void defaultTaskAttachmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTaskAttachmentShouldBeFound(shouldBeFound);
        defaultTaskAttachmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskAttachmentShouldBeFound(String filter) throws Exception {
        restTaskAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskAttachment.getId().intValue())));

        // Check, that the count call also returns 1
        restTaskAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskAttachmentShouldNotBeFound(String filter) throws Exception {
        restTaskAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTaskAttachment() throws Exception {
        // Get the taskAttachment
        restTaskAttachmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTaskAttachment() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskAttachment
        TaskAttachment updatedTaskAttachment = taskAttachmentRepository.findById(taskAttachment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTaskAttachment are not directly saved in db
        em.detach(updatedTaskAttachment);

        restTaskAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTaskAttachment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTaskAttachment))
            )
            .andExpect(status().isOk());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTaskAttachmentToMatchAllProperties(updatedTaskAttachment);
    }

    @Test
    @Transactional
    void putNonExistingTaskAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAttachment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskAttachment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaskAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaskAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskAttachmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskAttachment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskAttachment using partial update
        TaskAttachment partialUpdatedTaskAttachment = new TaskAttachment();
        partialUpdatedTaskAttachment.setId(taskAttachment.getId());

        restTaskAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskAttachment))
            )
            .andExpect(status().isOk());

        // Validate the TaskAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskAttachmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTaskAttachment, taskAttachment),
            getPersistedTaskAttachment(taskAttachment)
        );
    }

    @Test
    @Transactional
    void fullUpdateTaskAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskAttachment using partial update
        TaskAttachment partialUpdatedTaskAttachment = new TaskAttachment();
        partialUpdatedTaskAttachment.setId(taskAttachment.getId());

        restTaskAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskAttachment))
            )
            .andExpect(status().isOk());

        // Validate the TaskAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskAttachmentUpdatableFieldsEquals(partialUpdatedTaskAttachment, getPersistedTaskAttachment(partialUpdatedTaskAttachment));
    }

    @Test
    @Transactional
    void patchNonExistingTaskAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAttachment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaskAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaskAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskAttachmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(taskAttachment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTaskAttachment() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the taskAttachment
        restTaskAttachmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, taskAttachment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return taskAttachmentRepository.count();
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

    protected TaskAttachment getPersistedTaskAttachment(TaskAttachment taskAttachment) {
        return taskAttachmentRepository.findById(taskAttachment.getId()).orElseThrow();
    }

    protected void assertPersistedTaskAttachmentToMatchAllProperties(TaskAttachment expectedTaskAttachment) {
        assertTaskAttachmentAllPropertiesEquals(expectedTaskAttachment, getPersistedTaskAttachment(expectedTaskAttachment));
    }

    protected void assertPersistedTaskAttachmentToMatchUpdatableProperties(TaskAttachment expectedTaskAttachment) {
        assertTaskAttachmentAllUpdatablePropertiesEquals(expectedTaskAttachment, getPersistedTaskAttachment(expectedTaskAttachment));
    }
}
