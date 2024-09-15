package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.StatusAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.Status;
import in.beze.pilot.domain.StatusCategory;
import in.beze.pilot.repository.StatusRepository;
import in.beze.pilot.service.StatusService;
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
 * Integration tests for the {@link StatusResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StatusResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_SEQUENCE_NO = 1;
    private static final Integer UPDATED_SEQUENCE_NO = 2;
    private static final Integer SMALLER_SEQUENCE_NO = 1 - 1;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StatusRepository statusRepository;

    @Mock
    private StatusRepository statusRepositoryMock;

    @Mock
    private StatusService statusServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStatusMockMvc;

    private Status status;

    private Status insertedStatus;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Status createEntity() {
        return new Status().name(DEFAULT_NAME).sequenceNo(DEFAULT_SEQUENCE_NO).description(DEFAULT_DESCRIPTION).type(DEFAULT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Status createUpdatedEntity() {
        return new Status().name(UPDATED_NAME).sequenceNo(UPDATED_SEQUENCE_NO).description(UPDATED_DESCRIPTION).type(UPDATED_TYPE);
    }

    @BeforeEach
    public void initTest() {
        status = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedStatus != null) {
            statusRepository.delete(insertedStatus);
            insertedStatus = null;
        }
    }

    @Test
    @Transactional
    void createStatus() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Status
        var returnedStatus = om.readValue(
            restStatusMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(status)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Status.class
        );

        // Validate the Status in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStatusUpdatableFieldsEquals(returnedStatus, getPersistedStatus(returnedStatus));

        insertedStatus = returnedStatus;
    }

    @Test
    @Transactional
    void createStatusWithExistingId() throws Exception {
        // Create the Status with an existing ID
        status.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(status)))
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStatuses() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList
        restStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sequenceNo").value(hasItem(DEFAULT_SEQUENCE_NO)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStatusesWithEagerRelationshipsIsEnabled() throws Exception {
        when(statusServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStatusMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(statusServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStatusesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(statusServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStatusMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(statusRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStatus() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get the status
        restStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, status.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(status.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.sequenceNo").value(DEFAULT_SEQUENCE_NO))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
    }

    @Test
    @Transactional
    void getStatusesByIdFiltering() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        Long id = status.getId();

        defaultStatusFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStatusFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStatusFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStatusesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where name equals to
        defaultStatusFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatusesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where name in
        defaultStatusFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatusesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where name is not null
        defaultStatusFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where name contains
        defaultStatusFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatusesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where name does not contain
        defaultStatusFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllStatusesBySequenceNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where sequenceNo equals to
        defaultStatusFiltering("sequenceNo.equals=" + DEFAULT_SEQUENCE_NO, "sequenceNo.equals=" + UPDATED_SEQUENCE_NO);
    }

    @Test
    @Transactional
    void getAllStatusesBySequenceNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where sequenceNo in
        defaultStatusFiltering("sequenceNo.in=" + DEFAULT_SEQUENCE_NO + "," + UPDATED_SEQUENCE_NO, "sequenceNo.in=" + UPDATED_SEQUENCE_NO);
    }

    @Test
    @Transactional
    void getAllStatusesBySequenceNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where sequenceNo is not null
        defaultStatusFiltering("sequenceNo.specified=true", "sequenceNo.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusesBySequenceNoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where sequenceNo is greater than or equal to
        defaultStatusFiltering(
            "sequenceNo.greaterThanOrEqual=" + DEFAULT_SEQUENCE_NO,
            "sequenceNo.greaterThanOrEqual=" + UPDATED_SEQUENCE_NO
        );
    }

    @Test
    @Transactional
    void getAllStatusesBySequenceNoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where sequenceNo is less than or equal to
        defaultStatusFiltering("sequenceNo.lessThanOrEqual=" + DEFAULT_SEQUENCE_NO, "sequenceNo.lessThanOrEqual=" + SMALLER_SEQUENCE_NO);
    }

    @Test
    @Transactional
    void getAllStatusesBySequenceNoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where sequenceNo is less than
        defaultStatusFiltering("sequenceNo.lessThan=" + UPDATED_SEQUENCE_NO, "sequenceNo.lessThan=" + DEFAULT_SEQUENCE_NO);
    }

    @Test
    @Transactional
    void getAllStatusesBySequenceNoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where sequenceNo is greater than
        defaultStatusFiltering("sequenceNo.greaterThan=" + SMALLER_SEQUENCE_NO, "sequenceNo.greaterThan=" + DEFAULT_SEQUENCE_NO);
    }

    @Test
    @Transactional
    void getAllStatusesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where description equals to
        defaultStatusFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStatusesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where description in
        defaultStatusFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllStatusesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where description is not null
        defaultStatusFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where description contains
        defaultStatusFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStatusesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where description does not contain
        defaultStatusFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStatusesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where type equals to
        defaultStatusFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllStatusesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where type in
        defaultStatusFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllStatusesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where type is not null
        defaultStatusFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusesByTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where type contains
        defaultStatusFiltering("type.contains=" + DEFAULT_TYPE, "type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllStatusesByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        // Get all the statusList where type does not contain
        defaultStatusFiltering("type.doesNotContain=" + UPDATED_TYPE, "type.doesNotContain=" + DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void getAllStatusesByCategoryIsEqualToSomething() throws Exception {
        StatusCategory category;
        if (TestUtil.findAll(em, StatusCategory.class).isEmpty()) {
            statusRepository.saveAndFlush(status);
            category = StatusCategoryResourceIT.createEntity();
        } else {
            category = TestUtil.findAll(em, StatusCategory.class).get(0);
        }
        em.persist(category);
        em.flush();
        status.setCategory(category);
        statusRepository.saveAndFlush(status);
        Long categoryId = category.getId();
        // Get all the statusList where category equals to categoryId
        defaultStatusShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the statusList where category equals to (categoryId + 1)
        defaultStatusShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    private void defaultStatusFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStatusShouldBeFound(shouldBeFound);
        defaultStatusShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStatusShouldBeFound(String filter) throws Exception {
        restStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sequenceNo").value(hasItem(DEFAULT_SEQUENCE_NO)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));

        // Check, that the count call also returns 1
        restStatusMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStatusShouldNotBeFound(String filter) throws Exception {
        restStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStatusMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStatus() throws Exception {
        // Get the status
        restStatusMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStatus() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the status
        Status updatedStatus = statusRepository.findById(status.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStatus are not directly saved in db
        em.detach(updatedStatus);
        updatedStatus.name(UPDATED_NAME).sequenceNo(UPDATED_SEQUENCE_NO).description(UPDATED_DESCRIPTION).type(UPDATED_TYPE);

        restStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStatus.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStatus))
            )
            .andExpect(status().isOk());

        // Validate the Status in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStatusToMatchAllProperties(updatedStatus);
    }

    @Test
    @Transactional
    void putNonExistingStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        status.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(put(ENTITY_API_URL_ID, status.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(status)))
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        status.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(status))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        status.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(status)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Status in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStatusWithPatch() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the status using partial update
        Status partialUpdatedStatus = new Status();
        partialUpdatedStatus.setId(status.getId());

        partialUpdatedStatus.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).type(UPDATED_TYPE);

        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatus))
            )
            .andExpect(status().isOk());

        // Validate the Status in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatusUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedStatus, status), getPersistedStatus(status));
    }

    @Test
    @Transactional
    void fullUpdateStatusWithPatch() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the status using partial update
        Status partialUpdatedStatus = new Status();
        partialUpdatedStatus.setId(status.getId());

        partialUpdatedStatus.name(UPDATED_NAME).sequenceNo(UPDATED_SEQUENCE_NO).description(UPDATED_DESCRIPTION).type(UPDATED_TYPE);

        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatus))
            )
            .andExpect(status().isOk());

        // Validate the Status in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatusUpdatableFieldsEquals(partialUpdatedStatus, getPersistedStatus(partialUpdatedStatus));
    }

    @Test
    @Transactional
    void patchNonExistingStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        status.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, status.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(status))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        status.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(status))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        status.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(status)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Status in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStatus() throws Exception {
        // Initialize the database
        insertedStatus = statusRepository.saveAndFlush(status);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the status
        restStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, status.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return statusRepository.count();
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

    protected Status getPersistedStatus(Status status) {
        return statusRepository.findById(status.getId()).orElseThrow();
    }

    protected void assertPersistedStatusToMatchAllProperties(Status expectedStatus) {
        assertStatusAllPropertiesEquals(expectedStatus, getPersistedStatus(expectedStatus));
    }

    protected void assertPersistedStatusToMatchUpdatableProperties(Status expectedStatus) {
        assertStatusAllUpdatablePropertiesEquals(expectedStatus, getPersistedStatus(expectedStatus));
    }
}
