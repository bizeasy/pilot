package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.SprintAttachmentAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.SprintAttachment;
import in.beze.pilot.repository.SprintAttachmentRepository;
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
 * Integration tests for the {@link SprintAttachmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SprintAttachmentResourceIT {

    private static final String ENTITY_API_URL = "/api/sprint-attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SprintAttachmentRepository sprintAttachmentRepository;

    @Mock
    private SprintAttachmentRepository sprintAttachmentRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSprintAttachmentMockMvc;

    private SprintAttachment sprintAttachment;

    private SprintAttachment insertedSprintAttachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SprintAttachment createEntity() {
        return new SprintAttachment();
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SprintAttachment createUpdatedEntity() {
        return new SprintAttachment();
    }

    @BeforeEach
    public void initTest() {
        sprintAttachment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSprintAttachment != null) {
            sprintAttachmentRepository.delete(insertedSprintAttachment);
            insertedSprintAttachment = null;
        }
    }

    @Test
    @Transactional
    void createSprintAttachment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SprintAttachment
        var returnedSprintAttachment = om.readValue(
            restSprintAttachmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprintAttachment)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SprintAttachment.class
        );

        // Validate the SprintAttachment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSprintAttachmentUpdatableFieldsEquals(returnedSprintAttachment, getPersistedSprintAttachment(returnedSprintAttachment));

        insertedSprintAttachment = returnedSprintAttachment;
    }

    @Test
    @Transactional
    void createSprintAttachmentWithExistingId() throws Exception {
        // Create the SprintAttachment with an existing ID
        sprintAttachment.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSprintAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprintAttachment)))
            .andExpect(status().isBadRequest());

        // Validate the SprintAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSprintAttachments() throws Exception {
        // Initialize the database
        insertedSprintAttachment = sprintAttachmentRepository.saveAndFlush(sprintAttachment);

        // Get all the sprintAttachmentList
        restSprintAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sprintAttachment.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSprintAttachmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(sprintAttachmentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSprintAttachmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sprintAttachmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSprintAttachmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sprintAttachmentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSprintAttachmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sprintAttachmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSprintAttachment() throws Exception {
        // Initialize the database
        insertedSprintAttachment = sprintAttachmentRepository.saveAndFlush(sprintAttachment);

        // Get the sprintAttachment
        restSprintAttachmentMockMvc
            .perform(get(ENTITY_API_URL_ID, sprintAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sprintAttachment.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingSprintAttachment() throws Exception {
        // Get the sprintAttachment
        restSprintAttachmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSprintAttachment() throws Exception {
        // Initialize the database
        insertedSprintAttachment = sprintAttachmentRepository.saveAndFlush(sprintAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sprintAttachment
        SprintAttachment updatedSprintAttachment = sprintAttachmentRepository.findById(sprintAttachment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSprintAttachment are not directly saved in db
        em.detach(updatedSprintAttachment);

        restSprintAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSprintAttachment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSprintAttachment))
            )
            .andExpect(status().isOk());

        // Validate the SprintAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSprintAttachmentToMatchAllProperties(updatedSprintAttachment);
    }

    @Test
    @Transactional
    void putNonExistingSprintAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintAttachment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSprintAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sprintAttachment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sprintAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the SprintAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSprintAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sprintAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the SprintAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSprintAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintAttachmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprintAttachment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SprintAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSprintAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedSprintAttachment = sprintAttachmentRepository.saveAndFlush(sprintAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sprintAttachment using partial update
        SprintAttachment partialUpdatedSprintAttachment = new SprintAttachment();
        partialUpdatedSprintAttachment.setId(sprintAttachment.getId());

        restSprintAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSprintAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSprintAttachment))
            )
            .andExpect(status().isOk());

        // Validate the SprintAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSprintAttachmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSprintAttachment, sprintAttachment),
            getPersistedSprintAttachment(sprintAttachment)
        );
    }

    @Test
    @Transactional
    void fullUpdateSprintAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedSprintAttachment = sprintAttachmentRepository.saveAndFlush(sprintAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sprintAttachment using partial update
        SprintAttachment partialUpdatedSprintAttachment = new SprintAttachment();
        partialUpdatedSprintAttachment.setId(sprintAttachment.getId());

        restSprintAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSprintAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSprintAttachment))
            )
            .andExpect(status().isOk());

        // Validate the SprintAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSprintAttachmentUpdatableFieldsEquals(
            partialUpdatedSprintAttachment,
            getPersistedSprintAttachment(partialUpdatedSprintAttachment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSprintAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintAttachment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSprintAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sprintAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sprintAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the SprintAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSprintAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sprintAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the SprintAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSprintAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintAttachmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sprintAttachment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SprintAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSprintAttachment() throws Exception {
        // Initialize the database
        insertedSprintAttachment = sprintAttachmentRepository.saveAndFlush(sprintAttachment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sprintAttachment
        restSprintAttachmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, sprintAttachment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sprintAttachmentRepository.count();
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

    protected SprintAttachment getPersistedSprintAttachment(SprintAttachment sprintAttachment) {
        return sprintAttachmentRepository.findById(sprintAttachment.getId()).orElseThrow();
    }

    protected void assertPersistedSprintAttachmentToMatchAllProperties(SprintAttachment expectedSprintAttachment) {
        assertSprintAttachmentAllPropertiesEquals(expectedSprintAttachment, getPersistedSprintAttachment(expectedSprintAttachment));
    }

    protected void assertPersistedSprintAttachmentToMatchUpdatableProperties(SprintAttachment expectedSprintAttachment) {
        assertSprintAttachmentAllUpdatablePropertiesEquals(
            expectedSprintAttachment,
            getPersistedSprintAttachment(expectedSprintAttachment)
        );
    }
}
