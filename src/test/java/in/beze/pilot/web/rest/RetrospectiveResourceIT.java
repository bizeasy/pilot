package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.RetrospectiveAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.Retrospective;
import in.beze.pilot.domain.Sprint;
import in.beze.pilot.repository.RetrospectiveRepository;
import in.beze.pilot.service.RetrospectiveService;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link RetrospectiveResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RetrospectiveResourceIT {

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final String DEFAULT_ACTION_ITEMS = "AAAAAAAAAA";
    private static final String UPDATED_ACTION_ITEMS = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/retrospectives";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RetrospectiveRepository retrospectiveRepository;

    @Mock
    private RetrospectiveRepository retrospectiveRepositoryMock;

    @Mock
    private RetrospectiveService retrospectiveServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRetrospectiveMockMvc;

    private Retrospective retrospective;

    private Retrospective insertedRetrospective;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Retrospective createEntity() {
        return new Retrospective().summary(DEFAULT_SUMMARY).actionItems(DEFAULT_ACTION_ITEMS).dateCreated(DEFAULT_DATE_CREATED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Retrospective createUpdatedEntity() {
        return new Retrospective().summary(UPDATED_SUMMARY).actionItems(UPDATED_ACTION_ITEMS).dateCreated(UPDATED_DATE_CREATED);
    }

    @BeforeEach
    public void initTest() {
        retrospective = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedRetrospective != null) {
            retrospectiveRepository.delete(insertedRetrospective);
            insertedRetrospective = null;
        }
    }

    @Test
    @Transactional
    void createRetrospective() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Retrospective
        var returnedRetrospective = om.readValue(
            restRetrospectiveMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(retrospective)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Retrospective.class
        );

        // Validate the Retrospective in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRetrospectiveUpdatableFieldsEquals(returnedRetrospective, getPersistedRetrospective(returnedRetrospective));

        insertedRetrospective = returnedRetrospective;
    }

    @Test
    @Transactional
    void createRetrospectiveWithExistingId() throws Exception {
        // Create the Retrospective with an existing ID
        retrospective.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRetrospectiveMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(retrospective)))
            .andExpect(status().isBadRequest());

        // Validate the Retrospective in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateCreatedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        retrospective.setDateCreated(null);

        // Create the Retrospective, which fails.

        restRetrospectiveMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(retrospective)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRetrospectives() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList
        restRetrospectiveMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(retrospective.getId().intValue())))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].actionItems").value(hasItem(DEFAULT_ACTION_ITEMS)))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRetrospectivesWithEagerRelationshipsIsEnabled() throws Exception {
        when(retrospectiveServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRetrospectiveMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(retrospectiveServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRetrospectivesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(retrospectiveServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRetrospectiveMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(retrospectiveRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRetrospective() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get the retrospective
        restRetrospectiveMockMvc
            .perform(get(ENTITY_API_URL_ID, retrospective.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(retrospective.getId().intValue()))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY))
            .andExpect(jsonPath("$.actionItems").value(DEFAULT_ACTION_ITEMS))
            .andExpect(jsonPath("$.dateCreated").value(DEFAULT_DATE_CREATED.toString()));
    }

    @Test
    @Transactional
    void getRetrospectivesByIdFiltering() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        Long id = retrospective.getId();

        defaultRetrospectiveFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRetrospectiveFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRetrospectiveFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRetrospectivesBySummaryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where summary equals to
        defaultRetrospectiveFiltering("summary.equals=" + DEFAULT_SUMMARY, "summary.equals=" + UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    void getAllRetrospectivesBySummaryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where summary in
        defaultRetrospectiveFiltering("summary.in=" + DEFAULT_SUMMARY + "," + UPDATED_SUMMARY, "summary.in=" + UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    void getAllRetrospectivesBySummaryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where summary is not null
        defaultRetrospectiveFiltering("summary.specified=true", "summary.specified=false");
    }

    @Test
    @Transactional
    void getAllRetrospectivesBySummaryContainsSomething() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where summary contains
        defaultRetrospectiveFiltering("summary.contains=" + DEFAULT_SUMMARY, "summary.contains=" + UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    void getAllRetrospectivesBySummaryNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where summary does not contain
        defaultRetrospectiveFiltering("summary.doesNotContain=" + UPDATED_SUMMARY, "summary.doesNotContain=" + DEFAULT_SUMMARY);
    }

    @Test
    @Transactional
    void getAllRetrospectivesByActionItemsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where actionItems equals to
        defaultRetrospectiveFiltering("actionItems.equals=" + DEFAULT_ACTION_ITEMS, "actionItems.equals=" + UPDATED_ACTION_ITEMS);
    }

    @Test
    @Transactional
    void getAllRetrospectivesByActionItemsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where actionItems in
        defaultRetrospectiveFiltering(
            "actionItems.in=" + DEFAULT_ACTION_ITEMS + "," + UPDATED_ACTION_ITEMS,
            "actionItems.in=" + UPDATED_ACTION_ITEMS
        );
    }

    @Test
    @Transactional
    void getAllRetrospectivesByActionItemsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where actionItems is not null
        defaultRetrospectiveFiltering("actionItems.specified=true", "actionItems.specified=false");
    }

    @Test
    @Transactional
    void getAllRetrospectivesByActionItemsContainsSomething() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where actionItems contains
        defaultRetrospectiveFiltering("actionItems.contains=" + DEFAULT_ACTION_ITEMS, "actionItems.contains=" + UPDATED_ACTION_ITEMS);
    }

    @Test
    @Transactional
    void getAllRetrospectivesByActionItemsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where actionItems does not contain
        defaultRetrospectiveFiltering(
            "actionItems.doesNotContain=" + UPDATED_ACTION_ITEMS,
            "actionItems.doesNotContain=" + DEFAULT_ACTION_ITEMS
        );
    }

    @Test
    @Transactional
    void getAllRetrospectivesByDateCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where dateCreated equals to
        defaultRetrospectiveFiltering("dateCreated.equals=" + DEFAULT_DATE_CREATED, "dateCreated.equals=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllRetrospectivesByDateCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where dateCreated in
        defaultRetrospectiveFiltering(
            "dateCreated.in=" + DEFAULT_DATE_CREATED + "," + UPDATED_DATE_CREATED,
            "dateCreated.in=" + UPDATED_DATE_CREATED
        );
    }

    @Test
    @Transactional
    void getAllRetrospectivesByDateCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        // Get all the retrospectiveList where dateCreated is not null
        defaultRetrospectiveFiltering("dateCreated.specified=true", "dateCreated.specified=false");
    }

    @Test
    @Transactional
    void getAllRetrospectivesBySprintIsEqualToSomething() throws Exception {
        Sprint sprint;
        if (TestUtil.findAll(em, Sprint.class).isEmpty()) {
            retrospectiveRepository.saveAndFlush(retrospective);
            sprint = SprintResourceIT.createEntity();
        } else {
            sprint = TestUtil.findAll(em, Sprint.class).get(0);
        }
        em.persist(sprint);
        em.flush();
        retrospective.setSprint(sprint);
        retrospectiveRepository.saveAndFlush(retrospective);
        Long sprintId = sprint.getId();
        // Get all the retrospectiveList where sprint equals to sprintId
        defaultRetrospectiveShouldBeFound("sprintId.equals=" + sprintId);

        // Get all the retrospectiveList where sprint equals to (sprintId + 1)
        defaultRetrospectiveShouldNotBeFound("sprintId.equals=" + (sprintId + 1));
    }

    private void defaultRetrospectiveFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRetrospectiveShouldBeFound(shouldBeFound);
        defaultRetrospectiveShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRetrospectiveShouldBeFound(String filter) throws Exception {
        restRetrospectiveMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(retrospective.getId().intValue())))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].actionItems").value(hasItem(DEFAULT_ACTION_ITEMS)))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())));

        // Check, that the count call also returns 1
        restRetrospectiveMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRetrospectiveShouldNotBeFound(String filter) throws Exception {
        restRetrospectiveMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRetrospectiveMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRetrospective() throws Exception {
        // Get the retrospective
        restRetrospectiveMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRetrospective() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the retrospective
        Retrospective updatedRetrospective = retrospectiveRepository.findById(retrospective.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRetrospective are not directly saved in db
        em.detach(updatedRetrospective);
        updatedRetrospective.summary(UPDATED_SUMMARY).actionItems(UPDATED_ACTION_ITEMS).dateCreated(UPDATED_DATE_CREATED);

        restRetrospectiveMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRetrospective.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedRetrospective))
            )
            .andExpect(status().isOk());

        // Validate the Retrospective in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRetrospectiveToMatchAllProperties(updatedRetrospective);
    }

    @Test
    @Transactional
    void putNonExistingRetrospective() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        retrospective.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRetrospectiveMockMvc
            .perform(
                put(ENTITY_API_URL_ID, retrospective.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(retrospective))
            )
            .andExpect(status().isBadRequest());

        // Validate the Retrospective in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRetrospective() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        retrospective.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRetrospectiveMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(retrospective))
            )
            .andExpect(status().isBadRequest());

        // Validate the Retrospective in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRetrospective() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        retrospective.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRetrospectiveMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(retrospective)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Retrospective in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRetrospectiveWithPatch() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the retrospective using partial update
        Retrospective partialUpdatedRetrospective = new Retrospective();
        partialUpdatedRetrospective.setId(retrospective.getId());

        partialUpdatedRetrospective.summary(UPDATED_SUMMARY).dateCreated(UPDATED_DATE_CREATED);

        restRetrospectiveMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRetrospective.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRetrospective))
            )
            .andExpect(status().isOk());

        // Validate the Retrospective in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRetrospectiveUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRetrospective, retrospective),
            getPersistedRetrospective(retrospective)
        );
    }

    @Test
    @Transactional
    void fullUpdateRetrospectiveWithPatch() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the retrospective using partial update
        Retrospective partialUpdatedRetrospective = new Retrospective();
        partialUpdatedRetrospective.setId(retrospective.getId());

        partialUpdatedRetrospective.summary(UPDATED_SUMMARY).actionItems(UPDATED_ACTION_ITEMS).dateCreated(UPDATED_DATE_CREATED);

        restRetrospectiveMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRetrospective.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRetrospective))
            )
            .andExpect(status().isOk());

        // Validate the Retrospective in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRetrospectiveUpdatableFieldsEquals(partialUpdatedRetrospective, getPersistedRetrospective(partialUpdatedRetrospective));
    }

    @Test
    @Transactional
    void patchNonExistingRetrospective() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        retrospective.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRetrospectiveMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, retrospective.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(retrospective))
            )
            .andExpect(status().isBadRequest());

        // Validate the Retrospective in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRetrospective() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        retrospective.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRetrospectiveMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(retrospective))
            )
            .andExpect(status().isBadRequest());

        // Validate the Retrospective in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRetrospective() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        retrospective.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRetrospectiveMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(retrospective)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Retrospective in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRetrospective() throws Exception {
        // Initialize the database
        insertedRetrospective = retrospectiveRepository.saveAndFlush(retrospective);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the retrospective
        restRetrospectiveMockMvc
            .perform(delete(ENTITY_API_URL_ID, retrospective.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return retrospectiveRepository.count();
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

    protected Retrospective getPersistedRetrospective(Retrospective retrospective) {
        return retrospectiveRepository.findById(retrospective.getId()).orElseThrow();
    }

    protected void assertPersistedRetrospectiveToMatchAllProperties(Retrospective expectedRetrospective) {
        assertRetrospectiveAllPropertiesEquals(expectedRetrospective, getPersistedRetrospective(expectedRetrospective));
    }

    protected void assertPersistedRetrospectiveToMatchUpdatableProperties(Retrospective expectedRetrospective) {
        assertRetrospectiveAllUpdatablePropertiesEquals(expectedRetrospective, getPersistedRetrospective(expectedRetrospective));
    }
}
