package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.PartyAttachmentAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.Attachment;
import in.beze.pilot.domain.Party;
import in.beze.pilot.domain.PartyAttachment;
import in.beze.pilot.repository.PartyAttachmentRepository;
import in.beze.pilot.service.PartyAttachmentService;
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
 * Integration tests for the {@link PartyAttachmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PartyAttachmentResourceIT {

    private static final String ENTITY_API_URL = "/api/party-attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PartyAttachmentRepository partyAttachmentRepository;

    @Mock
    private PartyAttachmentRepository partyAttachmentRepositoryMock;

    @Mock
    private PartyAttachmentService partyAttachmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartyAttachmentMockMvc;

    private PartyAttachment partyAttachment;

    private PartyAttachment insertedPartyAttachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartyAttachment createEntity() {
        return new PartyAttachment();
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartyAttachment createUpdatedEntity() {
        return new PartyAttachment();
    }

    @BeforeEach
    public void initTest() {
        partyAttachment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPartyAttachment != null) {
            partyAttachmentRepository.delete(insertedPartyAttachment);
            insertedPartyAttachment = null;
        }
    }

    @Test
    @Transactional
    void createPartyAttachment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PartyAttachment
        var returnedPartyAttachment = om.readValue(
            restPartyAttachmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partyAttachment)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PartyAttachment.class
        );

        // Validate the PartyAttachment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPartyAttachmentUpdatableFieldsEquals(returnedPartyAttachment, getPersistedPartyAttachment(returnedPartyAttachment));

        insertedPartyAttachment = returnedPartyAttachment;
    }

    @Test
    @Transactional
    void createPartyAttachmentWithExistingId() throws Exception {
        // Create the PartyAttachment with an existing ID
        partyAttachment.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartyAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partyAttachment)))
            .andExpect(status().isBadRequest());

        // Validate the PartyAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPartyAttachments() throws Exception {
        // Initialize the database
        insertedPartyAttachment = partyAttachmentRepository.saveAndFlush(partyAttachment);

        // Get all the partyAttachmentList
        restPartyAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partyAttachment.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPartyAttachmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(partyAttachmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPartyAttachmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(partyAttachmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPartyAttachmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(partyAttachmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPartyAttachmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(partyAttachmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPartyAttachment() throws Exception {
        // Initialize the database
        insertedPartyAttachment = partyAttachmentRepository.saveAndFlush(partyAttachment);

        // Get the partyAttachment
        restPartyAttachmentMockMvc
            .perform(get(ENTITY_API_URL_ID, partyAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(partyAttachment.getId().intValue()));
    }

    @Test
    @Transactional
    void getPartyAttachmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPartyAttachment = partyAttachmentRepository.saveAndFlush(partyAttachment);

        Long id = partyAttachment.getId();

        defaultPartyAttachmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPartyAttachmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPartyAttachmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPartyAttachmentsByPartyIsEqualToSomething() throws Exception {
        Party party;
        if (TestUtil.findAll(em, Party.class).isEmpty()) {
            partyAttachmentRepository.saveAndFlush(partyAttachment);
            party = PartyResourceIT.createEntity();
        } else {
            party = TestUtil.findAll(em, Party.class).get(0);
        }
        em.persist(party);
        em.flush();
        partyAttachment.setParty(party);
        partyAttachmentRepository.saveAndFlush(partyAttachment);
        Long partyId = party.getId();
        // Get all the partyAttachmentList where party equals to partyId
        defaultPartyAttachmentShouldBeFound("partyId.equals=" + partyId);

        // Get all the partyAttachmentList where party equals to (partyId + 1)
        defaultPartyAttachmentShouldNotBeFound("partyId.equals=" + (partyId + 1));
    }

    @Test
    @Transactional
    void getAllPartyAttachmentsByAttachmentIsEqualToSomething() throws Exception {
        Attachment attachment;
        if (TestUtil.findAll(em, Attachment.class).isEmpty()) {
            partyAttachmentRepository.saveAndFlush(partyAttachment);
            attachment = AttachmentResourceIT.createEntity();
        } else {
            attachment = TestUtil.findAll(em, Attachment.class).get(0);
        }
        em.persist(attachment);
        em.flush();
        partyAttachment.setAttachment(attachment);
        partyAttachmentRepository.saveAndFlush(partyAttachment);
        Long attachmentId = attachment.getId();
        // Get all the partyAttachmentList where attachment equals to attachmentId
        defaultPartyAttachmentShouldBeFound("attachmentId.equals=" + attachmentId);

        // Get all the partyAttachmentList where attachment equals to (attachmentId + 1)
        defaultPartyAttachmentShouldNotBeFound("attachmentId.equals=" + (attachmentId + 1));
    }

    private void defaultPartyAttachmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPartyAttachmentShouldBeFound(shouldBeFound);
        defaultPartyAttachmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPartyAttachmentShouldBeFound(String filter) throws Exception {
        restPartyAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partyAttachment.getId().intValue())));

        // Check, that the count call also returns 1
        restPartyAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPartyAttachmentShouldNotBeFound(String filter) throws Exception {
        restPartyAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPartyAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPartyAttachment() throws Exception {
        // Get the partyAttachment
        restPartyAttachmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPartyAttachment() throws Exception {
        // Initialize the database
        insertedPartyAttachment = partyAttachmentRepository.saveAndFlush(partyAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partyAttachment
        PartyAttachment updatedPartyAttachment = partyAttachmentRepository.findById(partyAttachment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPartyAttachment are not directly saved in db
        em.detach(updatedPartyAttachment);

        restPartyAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPartyAttachment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPartyAttachment))
            )
            .andExpect(status().isOk());

        // Validate the PartyAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPartyAttachmentToMatchAllProperties(updatedPartyAttachment);
    }

    @Test
    @Transactional
    void putNonExistingPartyAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partyAttachment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartyAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partyAttachment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partyAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartyAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPartyAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partyAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartyAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partyAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartyAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPartyAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partyAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartyAttachmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partyAttachment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartyAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartyAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedPartyAttachment = partyAttachmentRepository.saveAndFlush(partyAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partyAttachment using partial update
        PartyAttachment partialUpdatedPartyAttachment = new PartyAttachment();
        partialUpdatedPartyAttachment.setId(partyAttachment.getId());

        restPartyAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartyAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartyAttachment))
            )
            .andExpect(status().isOk());

        // Validate the PartyAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartyAttachmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPartyAttachment, partyAttachment),
            getPersistedPartyAttachment(partyAttachment)
        );
    }

    @Test
    @Transactional
    void fullUpdatePartyAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedPartyAttachment = partyAttachmentRepository.saveAndFlush(partyAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partyAttachment using partial update
        PartyAttachment partialUpdatedPartyAttachment = new PartyAttachment();
        partialUpdatedPartyAttachment.setId(partyAttachment.getId());

        restPartyAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartyAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartyAttachment))
            )
            .andExpect(status().isOk());

        // Validate the PartyAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartyAttachmentUpdatableFieldsEquals(
            partialUpdatedPartyAttachment,
            getPersistedPartyAttachment(partialUpdatedPartyAttachment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPartyAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partyAttachment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartyAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partyAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partyAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartyAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPartyAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partyAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartyAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partyAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartyAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPartyAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partyAttachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartyAttachmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(partyAttachment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartyAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePartyAttachment() throws Exception {
        // Initialize the database
        insertedPartyAttachment = partyAttachmentRepository.saveAndFlush(partyAttachment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the partyAttachment
        restPartyAttachmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, partyAttachment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return partyAttachmentRepository.count();
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

    protected PartyAttachment getPersistedPartyAttachment(PartyAttachment partyAttachment) {
        return partyAttachmentRepository.findById(partyAttachment.getId()).orElseThrow();
    }

    protected void assertPersistedPartyAttachmentToMatchAllProperties(PartyAttachment expectedPartyAttachment) {
        assertPartyAttachmentAllPropertiesEquals(expectedPartyAttachment, getPersistedPartyAttachment(expectedPartyAttachment));
    }

    protected void assertPersistedPartyAttachmentToMatchUpdatableProperties(PartyAttachment expectedPartyAttachment) {
        assertPartyAttachmentAllUpdatablePropertiesEquals(expectedPartyAttachment, getPersistedPartyAttachment(expectedPartyAttachment));
    }
}
