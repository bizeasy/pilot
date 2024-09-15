package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.StatusCategoryAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.StatusCategory;
import in.beze.pilot.repository.StatusCategoryRepository;
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
 * Integration tests for the {@link StatusCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StatusCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/status-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StatusCategoryRepository statusCategoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStatusCategoryMockMvc;

    private StatusCategory statusCategory;

    private StatusCategory insertedStatusCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatusCategory createEntity() {
        return new StatusCategory().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatusCategory createUpdatedEntity() {
        return new StatusCategory().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        statusCategory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedStatusCategory != null) {
            statusCategoryRepository.delete(insertedStatusCategory);
            insertedStatusCategory = null;
        }
    }

    @Test
    @Transactional
    void createStatusCategory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StatusCategory
        var returnedStatusCategory = om.readValue(
            restStatusCategoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusCategory)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StatusCategory.class
        );

        // Validate the StatusCategory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStatusCategoryUpdatableFieldsEquals(returnedStatusCategory, getPersistedStatusCategory(returnedStatusCategory));

        insertedStatusCategory = returnedStatusCategory;
    }

    @Test
    @Transactional
    void createStatusCategoryWithExistingId() throws Exception {
        // Create the StatusCategory with an existing ID
        statusCategory.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatusCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusCategory)))
            .andExpect(status().isBadRequest());

        // Validate the StatusCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStatusCategories() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        // Get all the statusCategoryList
        restStatusCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statusCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getStatusCategory() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        // Get the statusCategory
        restStatusCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, statusCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(statusCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getStatusCategoriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        Long id = statusCategory.getId();

        defaultStatusCategoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStatusCategoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStatusCategoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStatusCategoriesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        // Get all the statusCategoryList where name equals to
        defaultStatusCategoryFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatusCategoriesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        // Get all the statusCategoryList where name in
        defaultStatusCategoryFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatusCategoriesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        // Get all the statusCategoryList where name is not null
        defaultStatusCategoryFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusCategoriesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        // Get all the statusCategoryList where name contains
        defaultStatusCategoryFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatusCategoriesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        // Get all the statusCategoryList where name does not contain
        defaultStatusCategoryFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllStatusCategoriesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        // Get all the statusCategoryList where description equals to
        defaultStatusCategoryFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStatusCategoriesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        // Get all the statusCategoryList where description in
        defaultStatusCategoryFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllStatusCategoriesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        // Get all the statusCategoryList where description is not null
        defaultStatusCategoryFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusCategoriesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        // Get all the statusCategoryList where description contains
        defaultStatusCategoryFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStatusCategoriesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        // Get all the statusCategoryList where description does not contain
        defaultStatusCategoryFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    private void defaultStatusCategoryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStatusCategoryShouldBeFound(shouldBeFound);
        defaultStatusCategoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStatusCategoryShouldBeFound(String filter) throws Exception {
        restStatusCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statusCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restStatusCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStatusCategoryShouldNotBeFound(String filter) throws Exception {
        restStatusCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStatusCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStatusCategory() throws Exception {
        // Get the statusCategory
        restStatusCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStatusCategory() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statusCategory
        StatusCategory updatedStatusCategory = statusCategoryRepository.findById(statusCategory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStatusCategory are not directly saved in db
        em.detach(updatedStatusCategory);
        updatedStatusCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restStatusCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStatusCategory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStatusCategory))
            )
            .andExpect(status().isOk());

        // Validate the StatusCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStatusCategoryToMatchAllProperties(updatedStatusCategory);
    }

    @Test
    @Transactional
    void putNonExistingStatusCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusCategory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statusCategory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statusCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatusCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusCategory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statusCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatusCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusCategory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusCategory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatusCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStatusCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statusCategory using partial update
        StatusCategory partialUpdatedStatusCategory = new StatusCategory();
        partialUpdatedStatusCategory.setId(statusCategory.getId());

        partialUpdatedStatusCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restStatusCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatusCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatusCategory))
            )
            .andExpect(status().isOk());

        // Validate the StatusCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatusCategoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStatusCategory, statusCategory),
            getPersistedStatusCategory(statusCategory)
        );
    }

    @Test
    @Transactional
    void fullUpdateStatusCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statusCategory using partial update
        StatusCategory partialUpdatedStatusCategory = new StatusCategory();
        partialUpdatedStatusCategory.setId(statusCategory.getId());

        partialUpdatedStatusCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restStatusCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatusCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatusCategory))
            )
            .andExpect(status().isOk());

        // Validate the StatusCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatusCategoryUpdatableFieldsEquals(partialUpdatedStatusCategory, getPersistedStatusCategory(partialUpdatedStatusCategory));
    }

    @Test
    @Transactional
    void patchNonExistingStatusCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusCategory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, statusCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statusCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatusCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusCategory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statusCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatusCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusCategory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusCategoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(statusCategory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatusCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStatusCategory() throws Exception {
        // Initialize the database
        insertedStatusCategory = statusCategoryRepository.saveAndFlush(statusCategory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the statusCategory
        restStatusCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, statusCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return statusCategoryRepository.count();
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

    protected StatusCategory getPersistedStatusCategory(StatusCategory statusCategory) {
        return statusCategoryRepository.findById(statusCategory.getId()).orElseThrow();
    }

    protected void assertPersistedStatusCategoryToMatchAllProperties(StatusCategory expectedStatusCategory) {
        assertStatusCategoryAllPropertiesEquals(expectedStatusCategory, getPersistedStatusCategory(expectedStatusCategory));
    }

    protected void assertPersistedStatusCategoryToMatchUpdatableProperties(StatusCategory expectedStatusCategory) {
        assertStatusCategoryAllUpdatablePropertiesEquals(expectedStatusCategory, getPersistedStatusCategory(expectedStatusCategory));
    }
}
