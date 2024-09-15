package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.SprintTaskHistoryAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.SprintTaskHistory;
import in.beze.pilot.domain.Status;
import in.beze.pilot.repository.SprintTaskHistoryRepository;
import in.beze.pilot.service.SprintTaskHistoryService;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link SprintTaskHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SprintTaskHistoryResourceIT {

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_FROM_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FROM_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_FROM_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_TO_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TO_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_TO_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/sprint-task-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SprintTaskHistoryRepository sprintTaskHistoryRepository;

    @Mock
    private SprintTaskHistoryRepository sprintTaskHistoryRepositoryMock;

    @Mock
    private SprintTaskHistoryService sprintTaskHistoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSprintTaskHistoryMockMvc;

    private SprintTaskHistory sprintTaskHistory;

    private SprintTaskHistory insertedSprintTaskHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SprintTaskHistory createEntity() {
        return new SprintTaskHistory().comments(DEFAULT_COMMENTS).fromDate(DEFAULT_FROM_DATE).toDate(DEFAULT_TO_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SprintTaskHistory createUpdatedEntity() {
        return new SprintTaskHistory().comments(UPDATED_COMMENTS).fromDate(UPDATED_FROM_DATE).toDate(UPDATED_TO_DATE);
    }

    @BeforeEach
    public void initTest() {
        sprintTaskHistory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSprintTaskHistory != null) {
            sprintTaskHistoryRepository.delete(insertedSprintTaskHistory);
            insertedSprintTaskHistory = null;
        }
    }

    @Test
    @Transactional
    void createSprintTaskHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SprintTaskHistory
        var returnedSprintTaskHistory = om.readValue(
            restSprintTaskHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprintTaskHistory)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SprintTaskHistory.class
        );

        // Validate the SprintTaskHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSprintTaskHistoryUpdatableFieldsEquals(returnedSprintTaskHistory, getPersistedSprintTaskHistory(returnedSprintTaskHistory));

        insertedSprintTaskHistory = returnedSprintTaskHistory;
    }

    @Test
    @Transactional
    void createSprintTaskHistoryWithExistingId() throws Exception {
        // Create the SprintTaskHistory with an existing ID
        sprintTaskHistory.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSprintTaskHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprintTaskHistory)))
            .andExpect(status().isBadRequest());

        // Validate the SprintTaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistories() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList
        restSprintTaskHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sprintTaskHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)))
            .andExpect(jsonPath("$.[*].fromDate").value(hasItem(DEFAULT_FROM_DATE.toString())))
            .andExpect(jsonPath("$.[*].toDate").value(hasItem(DEFAULT_TO_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSprintTaskHistoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(sprintTaskHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSprintTaskHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sprintTaskHistoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSprintTaskHistoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sprintTaskHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSprintTaskHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sprintTaskHistoryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSprintTaskHistory() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get the sprintTaskHistory
        restSprintTaskHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, sprintTaskHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sprintTaskHistory.getId().intValue()))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS))
            .andExpect(jsonPath("$.fromDate").value(DEFAULT_FROM_DATE.toString()))
            .andExpect(jsonPath("$.toDate").value(DEFAULT_TO_DATE.toString()));
    }

    @Test
    @Transactional
    void getSprintTaskHistoriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        Long id = sprintTaskHistory.getId();

        defaultSprintTaskHistoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSprintTaskHistoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSprintTaskHistoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByCommentsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where comments equals to
        defaultSprintTaskHistoryFiltering("comments.equals=" + DEFAULT_COMMENTS, "comments.equals=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByCommentsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where comments in
        defaultSprintTaskHistoryFiltering("comments.in=" + DEFAULT_COMMENTS + "," + UPDATED_COMMENTS, "comments.in=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByCommentsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where comments is not null
        defaultSprintTaskHistoryFiltering("comments.specified=true", "comments.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByCommentsContainsSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where comments contains
        defaultSprintTaskHistoryFiltering("comments.contains=" + DEFAULT_COMMENTS, "comments.contains=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByCommentsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where comments does not contain
        defaultSprintTaskHistoryFiltering("comments.doesNotContain=" + UPDATED_COMMENTS, "comments.doesNotContain=" + DEFAULT_COMMENTS);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByFromDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where fromDate equals to
        defaultSprintTaskHistoryFiltering("fromDate.equals=" + DEFAULT_FROM_DATE, "fromDate.equals=" + UPDATED_FROM_DATE);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByFromDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where fromDate in
        defaultSprintTaskHistoryFiltering("fromDate.in=" + DEFAULT_FROM_DATE + "," + UPDATED_FROM_DATE, "fromDate.in=" + UPDATED_FROM_DATE);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByFromDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where fromDate is not null
        defaultSprintTaskHistoryFiltering("fromDate.specified=true", "fromDate.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByFromDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where fromDate is greater than or equal to
        defaultSprintTaskHistoryFiltering(
            "fromDate.greaterThanOrEqual=" + DEFAULT_FROM_DATE,
            "fromDate.greaterThanOrEqual=" + UPDATED_FROM_DATE
        );
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByFromDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where fromDate is less than or equal to
        defaultSprintTaskHistoryFiltering("fromDate.lessThanOrEqual=" + DEFAULT_FROM_DATE, "fromDate.lessThanOrEqual=" + SMALLER_FROM_DATE);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByFromDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where fromDate is less than
        defaultSprintTaskHistoryFiltering("fromDate.lessThan=" + UPDATED_FROM_DATE, "fromDate.lessThan=" + DEFAULT_FROM_DATE);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByFromDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where fromDate is greater than
        defaultSprintTaskHistoryFiltering("fromDate.greaterThan=" + SMALLER_FROM_DATE, "fromDate.greaterThan=" + DEFAULT_FROM_DATE);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByToDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where toDate equals to
        defaultSprintTaskHistoryFiltering("toDate.equals=" + DEFAULT_TO_DATE, "toDate.equals=" + UPDATED_TO_DATE);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByToDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where toDate in
        defaultSprintTaskHistoryFiltering("toDate.in=" + DEFAULT_TO_DATE + "," + UPDATED_TO_DATE, "toDate.in=" + UPDATED_TO_DATE);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByToDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where toDate is not null
        defaultSprintTaskHistoryFiltering("toDate.specified=true", "toDate.specified=false");
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByToDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where toDate is greater than or equal to
        defaultSprintTaskHistoryFiltering("toDate.greaterThanOrEqual=" + DEFAULT_TO_DATE, "toDate.greaterThanOrEqual=" + UPDATED_TO_DATE);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByToDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where toDate is less than or equal to
        defaultSprintTaskHistoryFiltering("toDate.lessThanOrEqual=" + DEFAULT_TO_DATE, "toDate.lessThanOrEqual=" + SMALLER_TO_DATE);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByToDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where toDate is less than
        defaultSprintTaskHistoryFiltering("toDate.lessThan=" + UPDATED_TO_DATE, "toDate.lessThan=" + DEFAULT_TO_DATE);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByToDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        // Get all the sprintTaskHistoryList where toDate is greater than
        defaultSprintTaskHistoryFiltering("toDate.greaterThan=" + SMALLER_TO_DATE, "toDate.greaterThan=" + DEFAULT_TO_DATE);
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByFromStatusIsEqualToSomething() throws Exception {
        Status fromStatus;
        if (TestUtil.findAll(em, Status.class).isEmpty()) {
            sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);
            fromStatus = StatusResourceIT.createEntity();
        } else {
            fromStatus = TestUtil.findAll(em, Status.class).get(0);
        }
        em.persist(fromStatus);
        em.flush();
        sprintTaskHistory.setFromStatus(fromStatus);
        sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);
        Long fromStatusId = fromStatus.getId();
        // Get all the sprintTaskHistoryList where fromStatus equals to fromStatusId
        defaultSprintTaskHistoryShouldBeFound("fromStatusId.equals=" + fromStatusId);

        // Get all the sprintTaskHistoryList where fromStatus equals to (fromStatusId + 1)
        defaultSprintTaskHistoryShouldNotBeFound("fromStatusId.equals=" + (fromStatusId + 1));
    }

    @Test
    @Transactional
    void getAllSprintTaskHistoriesByToStatusIsEqualToSomething() throws Exception {
        Status toStatus;
        if (TestUtil.findAll(em, Status.class).isEmpty()) {
            sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);
            toStatus = StatusResourceIT.createEntity();
        } else {
            toStatus = TestUtil.findAll(em, Status.class).get(0);
        }
        em.persist(toStatus);
        em.flush();
        sprintTaskHistory.setToStatus(toStatus);
        sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);
        Long toStatusId = toStatus.getId();
        // Get all the sprintTaskHistoryList where toStatus equals to toStatusId
        defaultSprintTaskHistoryShouldBeFound("toStatusId.equals=" + toStatusId);

        // Get all the sprintTaskHistoryList where toStatus equals to (toStatusId + 1)
        defaultSprintTaskHistoryShouldNotBeFound("toStatusId.equals=" + (toStatusId + 1));
    }

    private void defaultSprintTaskHistoryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSprintTaskHistoryShouldBeFound(shouldBeFound);
        defaultSprintTaskHistoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSprintTaskHistoryShouldBeFound(String filter) throws Exception {
        restSprintTaskHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sprintTaskHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)))
            .andExpect(jsonPath("$.[*].fromDate").value(hasItem(DEFAULT_FROM_DATE.toString())))
            .andExpect(jsonPath("$.[*].toDate").value(hasItem(DEFAULT_TO_DATE.toString())));

        // Check, that the count call also returns 1
        restSprintTaskHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSprintTaskHistoryShouldNotBeFound(String filter) throws Exception {
        restSprintTaskHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSprintTaskHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSprintTaskHistory() throws Exception {
        // Get the sprintTaskHistory
        restSprintTaskHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSprintTaskHistory() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sprintTaskHistory
        SprintTaskHistory updatedSprintTaskHistory = sprintTaskHistoryRepository.findById(sprintTaskHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSprintTaskHistory are not directly saved in db
        em.detach(updatedSprintTaskHistory);
        updatedSprintTaskHistory.comments(UPDATED_COMMENTS).fromDate(UPDATED_FROM_DATE).toDate(UPDATED_TO_DATE);

        restSprintTaskHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSprintTaskHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSprintTaskHistory))
            )
            .andExpect(status().isOk());

        // Validate the SprintTaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSprintTaskHistoryToMatchAllProperties(updatedSprintTaskHistory);
    }

    @Test
    @Transactional
    void putNonExistingSprintTaskHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintTaskHistory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSprintTaskHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sprintTaskHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sprintTaskHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the SprintTaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSprintTaskHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintTaskHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintTaskHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sprintTaskHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the SprintTaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSprintTaskHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintTaskHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintTaskHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sprintTaskHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SprintTaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSprintTaskHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sprintTaskHistory using partial update
        SprintTaskHistory partialUpdatedSprintTaskHistory = new SprintTaskHistory();
        partialUpdatedSprintTaskHistory.setId(sprintTaskHistory.getId());

        partialUpdatedSprintTaskHistory.comments(UPDATED_COMMENTS).toDate(UPDATED_TO_DATE);

        restSprintTaskHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSprintTaskHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSprintTaskHistory))
            )
            .andExpect(status().isOk());

        // Validate the SprintTaskHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSprintTaskHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSprintTaskHistory, sprintTaskHistory),
            getPersistedSprintTaskHistory(sprintTaskHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateSprintTaskHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sprintTaskHistory using partial update
        SprintTaskHistory partialUpdatedSprintTaskHistory = new SprintTaskHistory();
        partialUpdatedSprintTaskHistory.setId(sprintTaskHistory.getId());

        partialUpdatedSprintTaskHistory.comments(UPDATED_COMMENTS).fromDate(UPDATED_FROM_DATE).toDate(UPDATED_TO_DATE);

        restSprintTaskHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSprintTaskHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSprintTaskHistory))
            )
            .andExpect(status().isOk());

        // Validate the SprintTaskHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSprintTaskHistoryUpdatableFieldsEquals(
            partialUpdatedSprintTaskHistory,
            getPersistedSprintTaskHistory(partialUpdatedSprintTaskHistory)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSprintTaskHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintTaskHistory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSprintTaskHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sprintTaskHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sprintTaskHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the SprintTaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSprintTaskHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintTaskHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintTaskHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sprintTaskHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the SprintTaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSprintTaskHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sprintTaskHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSprintTaskHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sprintTaskHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SprintTaskHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSprintTaskHistory() throws Exception {
        // Initialize the database
        insertedSprintTaskHistory = sprintTaskHistoryRepository.saveAndFlush(sprintTaskHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sprintTaskHistory
        restSprintTaskHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, sprintTaskHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sprintTaskHistoryRepository.count();
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

    protected SprintTaskHistory getPersistedSprintTaskHistory(SprintTaskHistory sprintTaskHistory) {
        return sprintTaskHistoryRepository.findById(sprintTaskHistory.getId()).orElseThrow();
    }

    protected void assertPersistedSprintTaskHistoryToMatchAllProperties(SprintTaskHistory expectedSprintTaskHistory) {
        assertSprintTaskHistoryAllPropertiesEquals(expectedSprintTaskHistory, getPersistedSprintTaskHistory(expectedSprintTaskHistory));
    }

    protected void assertPersistedSprintTaskHistoryToMatchUpdatableProperties(SprintTaskHistory expectedSprintTaskHistory) {
        assertSprintTaskHistoryAllUpdatablePropertiesEquals(
            expectedSprintTaskHistory,
            getPersistedSprintTaskHistory(expectedSprintTaskHistory)
        );
    }
}
