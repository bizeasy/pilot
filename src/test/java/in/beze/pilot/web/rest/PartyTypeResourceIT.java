package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.PartyTypeAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.PartyType;
import in.beze.pilot.repository.PartyTypeRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PartyTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PartyTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/party-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PartyTypeRepository partyTypeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartyTypeMockMvc;

    private PartyType partyType;

    private PartyType insertedPartyType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartyType createEntity() {
        return new PartyType().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartyType createUpdatedEntity() {
        return new PartyType().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        partyType = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPartyType != null) {
            partyTypeRepository.delete(insertedPartyType);
            insertedPartyType = null;
        }
    }

    @Test
    @Transactional
    void createPartyType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PartyType
        var returnedPartyType = om.readValue(
            restPartyTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partyType)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PartyType.class
        );

        // Validate the PartyType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPartyTypeUpdatableFieldsEquals(returnedPartyType, getPersistedPartyType(returnedPartyType));

        insertedPartyType = returnedPartyType;
    }

    @Test
    @Transactional
    void createPartyTypeWithExistingId() throws Exception {
        // Create the PartyType with an existing ID
        partyType.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartyTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partyType)))
            .andExpect(status().isBadRequest());

        // Validate the PartyType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPartyTypes() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        // Get all the partyTypeList
        restPartyTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partyType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getPartyType() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        // Get the partyType
        restPartyTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, partyType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(partyType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getPartyTypesByIdFiltering() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        Long id = partyType.getId();

        defaultPartyTypeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPartyTypeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPartyTypeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPartyTypesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        // Get all the partyTypeList where name equals to
        defaultPartyTypeFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPartyTypesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        // Get all the partyTypeList where name in
        defaultPartyTypeFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPartyTypesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        // Get all the partyTypeList where name is not null
        defaultPartyTypeFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllPartyTypesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        // Get all the partyTypeList where name contains
        defaultPartyTypeFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPartyTypesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        // Get all the partyTypeList where name does not contain
        defaultPartyTypeFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllPartyTypesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        // Get all the partyTypeList where description equals to
        defaultPartyTypeFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPartyTypesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        // Get all the partyTypeList where description in
        defaultPartyTypeFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPartyTypesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        // Get all the partyTypeList where description is not null
        defaultPartyTypeFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllPartyTypesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        // Get all the partyTypeList where description contains
        defaultPartyTypeFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPartyTypesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        // Get all the partyTypeList where description does not contain
        defaultPartyTypeFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    private void defaultPartyTypeFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPartyTypeShouldBeFound(shouldBeFound);
        defaultPartyTypeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPartyTypeShouldBeFound(String filter) throws Exception {
        restPartyTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partyType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restPartyTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPartyTypeShouldNotBeFound(String filter) throws Exception {
        restPartyTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPartyTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPartyType() throws Exception {
        // Get the partyType
        restPartyTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPartyType() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partyType
        PartyType updatedPartyType = partyTypeRepository.findById(partyType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPartyType are not directly saved in db
        em.detach(updatedPartyType);
        updatedPartyType.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restPartyTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPartyType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPartyType))
            )
            .andExpect(status().isOk());

        // Validate the PartyType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPartyTypeToMatchAllProperties(updatedPartyType);
    }

    @Test
    @Transactional
    void putNonExistingPartyType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partyType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartyTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partyType.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partyType))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartyType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPartyType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partyType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartyTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partyType))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartyType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPartyType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partyType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartyTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partyType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartyType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartyTypeWithPatch() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partyType using partial update
        PartyType partialUpdatedPartyType = new PartyType();
        partialUpdatedPartyType.setId(partyType.getId());

        restPartyTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartyType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartyType))
            )
            .andExpect(status().isOk());

        // Validate the PartyType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartyTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPartyType, partyType),
            getPersistedPartyType(partyType)
        );
    }

    @Test
    @Transactional
    void fullUpdatePartyTypeWithPatch() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partyType using partial update
        PartyType partialUpdatedPartyType = new PartyType();
        partialUpdatedPartyType.setId(partyType.getId());

        partialUpdatedPartyType.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restPartyTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartyType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartyType))
            )
            .andExpect(status().isOk());

        // Validate the PartyType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartyTypeUpdatableFieldsEquals(partialUpdatedPartyType, getPersistedPartyType(partialUpdatedPartyType));
    }

    @Test
    @Transactional
    void patchNonExistingPartyType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partyType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartyTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partyType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partyType))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartyType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPartyType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partyType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartyTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partyType))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartyType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPartyType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partyType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartyTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(partyType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartyType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePartyType() throws Exception {
        // Initialize the database
        insertedPartyType = partyTypeRepository.saveAndFlush(partyType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the partyType
        restPartyTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, partyType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return partyTypeRepository.count();
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

    protected PartyType getPersistedPartyType(PartyType partyType) {
        return partyTypeRepository.findById(partyType.getId()).orElseThrow();
    }

    protected void assertPersistedPartyTypeToMatchAllProperties(PartyType expectedPartyType) {
        assertPartyTypeAllPropertiesEquals(expectedPartyType, getPersistedPartyType(expectedPartyType));
    }

    protected void assertPersistedPartyTypeToMatchUpdatableProperties(PartyType expectedPartyType) {
        assertPartyTypeAllUpdatablePropertiesEquals(expectedPartyType, getPersistedPartyType(expectedPartyType));
    }
}
