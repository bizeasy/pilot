package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.ProjectAttachmentAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.Attachment;
import in.beze.pilot.domain.Project;
import in.beze.pilot.domain.ProjectAttachment;
import in.beze.pilot.repository.ProjectAttachmentRepository;
import in.beze.pilot.service.ProjectAttachmentService;
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
 * Integration tests for the {@link ProjectAttachmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProjectAttachmentResourceIT {

    private static final String ENTITY_API_URL = "/api/project-attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProjectAttachmentRepository projectAttachmentRepository;

    @Mock
    private ProjectAttachmentRepository projectAttachmentRepositoryMock;

    @Mock
    private ProjectAttachmentService projectAttachmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectAttachmentMockMvc;

    private ProjectAttachment projectAttachment;

    private ProjectAttachment insertedProjectAttachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectAttachment createEntity() {
        return new ProjectAttachment();
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectAttachment createUpdatedEntity() {
        return new ProjectAttachment();
    }

    @BeforeEach
    public void initTest() {
        projectAttachment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProjectAttachment != null) {
            projectAttachmentRepository.delete(insertedProjectAttachment);
            insertedProjectAttachment = null;
        }
    }

    @Test
    @Transactional
    void createProjectAttachment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProjectAttachment
        var returnedProjectAttachment = om.readValue(
            restProjectAttachmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectAttachment)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProjectAttachment.class
        );

        // Validate the ProjectAttachment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProjectAttachmentUpdatableFieldsEquals(returnedProjectAttachment, getPersistedProjectAttachment(returnedProjectAttachment));

        insertedProjectAttachment = returnedProjectAttachment;
    }

    @Test
    @Transactional
    void createProjectAttachmentWithExistingId() throws Exception {
        // Create the ProjectAttachment with an existing ID
        projectAttachment.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectAttachment)))
            .andExpect(status().isBadRequest());

        // Validate the ProjectAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProjectAttachments() throws Exception {
        // Initialize the database
        insertedProjectAttachment = projectAttachmentRepository.saveAndFlush(projectAttachment);

        // Get all the projectAttachmentList
        restProjectAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectAttachment.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectAttachmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(projectAttachmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProjectAttachmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(projectAttachmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectAttachmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(projectAttachmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProjectAttachmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(projectAttachmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProjectAttachment() throws Exception {
        // Initialize the database
        insertedProjectAttachment = projectAttachmentRepository.saveAndFlush(projectAttachment);

        // Get the projectAttachment
        restProjectAttachmentMockMvc
            .perform(get(ENTITY_API_URL_ID, projectAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectAttachment.getId().intValue()));
    }

    @Test
    @Transactional
    void getProjectAttachmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedProjectAttachment = projectAttachmentRepository.saveAndFlush(projectAttachment);

        Long id = projectAttachment.getId();

        defaultProjectAttachmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProjectAttachmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProjectAttachmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProjectAttachmentsByFacilityIsEqualToSomething() throws Exception {
        Project facility;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            projectAttachmentRepository.saveAndFlush(projectAttachment);
            facility = ProjectResourceIT.createEntity();
        } else {
            facility = TestUtil.findAll(em, Project.class).get(0);
        }
        em.persist(facility);
        em.flush();
        projectAttachment.setFacility(facility);
        projectAttachmentRepository.saveAndFlush(projectAttachment);
        Long facilityId = facility.getId();
        // Get all the projectAttachmentList where facility equals to facilityId
        defaultProjectAttachmentShouldBeFound("facilityId.equals=" + facilityId);

        // Get all the projectAttachmentList where facility equals to (facilityId + 1)
        defaultProjectAttachmentShouldNotBeFound("facilityId.equals=" + (facilityId + 1));
    }

    @Test
    @Transactional
    void getAllProjectAttachmentsByAttachmentIsEqualToSomething() throws Exception {
        Attachment attachment;
        if (TestUtil.findAll(em, Attachment.class).isEmpty()) {
            projectAttachmentRepository.saveAndFlush(projectAttachment);
            attachment = AttachmentResourceIT.createEntity();
        } else {
            attachment = TestUtil.findAll(em, Attachment.class).get(0);
        }
        em.persist(attachment);
        em.flush();
        projectAttachment.setAttachment(attachment);
        projectAttachmentRepository.saveAndFlush(projectAttachment);
        Long attachmentId = attachment.getId();
        // Get all the projectAttachmentList where attachment equals to attachmentId
        defaultProjectAttachmentShouldBeFound("attachmentId.equals=" + attachmentId);

        // Get all the projectAttachmentList where attachment equals to (attachmentId + 1)
        defaultProjectAttachmentShouldNotBeFound("attachmentId.equals=" + (attachmentId + 1));
    }

    private void defaultProjectAttachmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProjectAttachmentShouldBeFound(shouldBeFound);
        defaultProjectAttachmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectAttachmentShouldBeFound(String filter) throws Exception {
        restProjectAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectAttachment.getId().intValue())));

        // Check, that the count call also returns 1
        restProjectAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectAttachmentShouldNotBeFound(String filter) throws Exception {
        restProjectAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProjectAttachment() throws Exception {
        // Get the projectAttachment
        restProjectAttachmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProjectAttachment() throws Exception {
        // Initialize the database
        insertedProjectAttachment = projectAttachmentRepository.saveAndFlush(projectAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectAttachment
        ProjectAttachment updatedProjectAttachment = projectAttachmentRepository.findById(projectAttachment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProjectAttachment are not directly saved in db
        em.detach(updatedProjectAttachment);

        restProjectAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProjectAttachment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProjectAttachment))
            )
            .andExpect(status().isOk());

        // Validate the ProjectAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProjectAttachmentToMatchAllProperties(updatedProjectAttachment);
    }

    @Test
    @Transactional
    void putNonExistingProjectAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectAttachment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectAttachment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjectAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjectAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectAttachmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectAttachment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProjectAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedProjectAttachment = projectAttachmentRepository.saveAndFlush(projectAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectAttachment using partial update
        ProjectAttachment partialUpdatedProjectAttachment = new ProjectAttachment();
        partialUpdatedProjectAttachment.setId(projectAttachment.getId());

        restProjectAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProjectAttachment))
            )
            .andExpect(status().isOk());

        // Validate the ProjectAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectAttachmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProjectAttachment, projectAttachment),
            getPersistedProjectAttachment(projectAttachment)
        );
    }

    @Test
    @Transactional
    void fullUpdateProjectAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedProjectAttachment = projectAttachmentRepository.saveAndFlush(projectAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectAttachment using partial update
        ProjectAttachment partialUpdatedProjectAttachment = new ProjectAttachment();
        partialUpdatedProjectAttachment.setId(projectAttachment.getId());

        restProjectAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProjectAttachment))
            )
            .andExpect(status().isOk());

        // Validate the ProjectAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectAttachmentUpdatableFieldsEquals(
            partialUpdatedProjectAttachment,
            getPersistedProjectAttachment(partialUpdatedProjectAttachment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingProjectAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectAttachment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjectAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjectAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectAttachmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(projectAttachment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProjectAttachment() throws Exception {
        // Initialize the database
        insertedProjectAttachment = projectAttachmentRepository.saveAndFlush(projectAttachment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the projectAttachment
        restProjectAttachmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, projectAttachment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return projectAttachmentRepository.count();
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

    protected ProjectAttachment getPersistedProjectAttachment(ProjectAttachment projectAttachment) {
        return projectAttachmentRepository.findById(projectAttachment.getId()).orElseThrow();
    }

    protected void assertPersistedProjectAttachmentToMatchAllProperties(ProjectAttachment expectedProjectAttachment) {
        assertProjectAttachmentAllPropertiesEquals(expectedProjectAttachment, getPersistedProjectAttachment(expectedProjectAttachment));
    }

    protected void assertPersistedProjectAttachmentToMatchUpdatableProperties(ProjectAttachment expectedProjectAttachment) {
        assertProjectAttachmentAllUpdatablePropertiesEquals(
            expectedProjectAttachment,
            getPersistedProjectAttachment(expectedProjectAttachment)
        );
    }
}
