package in.beze.pilot.web.rest;

import static in.beze.pilot.domain.PartyAsserts.*;
import static in.beze.pilot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.beze.pilot.IntegrationTest;
import in.beze.pilot.domain.Party;
import in.beze.pilot.domain.PartyType;
import in.beze.pilot.domain.Status;
import in.beze.pilot.domain.User;
import in.beze.pilot.repository.PartyRepository;
import in.beze.pilot.repository.UserRepository;
import in.beze.pilot.service.PartyService;
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
 * Integration tests for the {@link PartyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PartyResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DISPLAY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DOB = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DOB = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DOB = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_EMPLOYEE_ID = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYEE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/parties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PartyRepository partyRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private PartyRepository partyRepositoryMock;

    @Mock
    private PartyService partyServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartyMockMvc;

    private Party party;

    private Party insertedParty;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Party createEntity() {
        return new Party()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .displayName(DEFAULT_DISPLAY_NAME)
            .email(DEFAULT_EMAIL)
            .dob(DEFAULT_DOB)
            .notes(DEFAULT_NOTES)
            .mobileNumber(DEFAULT_MOBILE_NUMBER)
            .employeeId(DEFAULT_EMPLOYEE_ID)
            .login(DEFAULT_LOGIN);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Party createUpdatedEntity() {
        return new Party()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .displayName(UPDATED_DISPLAY_NAME)
            .email(UPDATED_EMAIL)
            .dob(UPDATED_DOB)
            .notes(UPDATED_NOTES)
            .mobileNumber(UPDATED_MOBILE_NUMBER)
            .employeeId(UPDATED_EMPLOYEE_ID)
            .login(UPDATED_LOGIN);
    }

    @BeforeEach
    public void initTest() {
        party = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedParty != null) {
            partyRepository.delete(insertedParty);
            insertedParty = null;
        }
    }

    @Test
    @Transactional
    void createParty() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Party
        var returnedParty = om.readValue(
            restPartyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(party)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Party.class
        );

        // Validate the Party in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPartyUpdatableFieldsEquals(returnedParty, getPersistedParty(returnedParty));

        insertedParty = returnedParty;
    }

    @Test
    @Transactional
    void createPartyWithExistingId() throws Exception {
        // Create the Party with an existing ID
        party.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(party)))
            .andExpect(status().isBadRequest());

        // Validate the Party in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMobileNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        party.setMobileNumber(null);

        // Create the Party, which fails.

        restPartyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(party)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllParties() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList
        restPartyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(party.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].mobileNumber").value(hasItem(DEFAULT_MOBILE_NUMBER)))
            .andExpect(jsonPath("$.[*].employeeId").value(hasItem(DEFAULT_EMPLOYEE_ID)))
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPartiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(partyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPartyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(partyServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPartiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(partyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPartyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(partyRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getParty() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get the party
        restPartyMockMvc
            .perform(get(ENTITY_API_URL_ID, party.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(party.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.displayName").value(DEFAULT_DISPLAY_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.dob").value(DEFAULT_DOB.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.mobileNumber").value(DEFAULT_MOBILE_NUMBER))
            .andExpect(jsonPath("$.employeeId").value(DEFAULT_EMPLOYEE_ID))
            .andExpect(jsonPath("$.login").value(DEFAULT_LOGIN));
    }

    @Test
    @Transactional
    void getPartiesByIdFiltering() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        Long id = party.getId();

        defaultPartyFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPartyFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPartyFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPartiesByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where firstName equals to
        defaultPartyFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPartiesByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where firstName in
        defaultPartyFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPartiesByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where firstName is not null
        defaultPartyFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllPartiesByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where firstName contains
        defaultPartyFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPartiesByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where firstName does not contain
        defaultPartyFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPartiesByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where lastName equals to
        defaultPartyFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPartiesByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where lastName in
        defaultPartyFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPartiesByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where lastName is not null
        defaultPartyFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllPartiesByLastNameContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where lastName contains
        defaultPartyFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPartiesByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where lastName does not contain
        defaultPartyFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPartiesByDisplayNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where displayName equals to
        defaultPartyFiltering("displayName.equals=" + DEFAULT_DISPLAY_NAME, "displayName.equals=" + UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllPartiesByDisplayNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where displayName in
        defaultPartyFiltering(
            "displayName.in=" + DEFAULT_DISPLAY_NAME + "," + UPDATED_DISPLAY_NAME,
            "displayName.in=" + UPDATED_DISPLAY_NAME
        );
    }

    @Test
    @Transactional
    void getAllPartiesByDisplayNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where displayName is not null
        defaultPartyFiltering("displayName.specified=true", "displayName.specified=false");
    }

    @Test
    @Transactional
    void getAllPartiesByDisplayNameContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where displayName contains
        defaultPartyFiltering("displayName.contains=" + DEFAULT_DISPLAY_NAME, "displayName.contains=" + UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllPartiesByDisplayNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where displayName does not contain
        defaultPartyFiltering("displayName.doesNotContain=" + UPDATED_DISPLAY_NAME, "displayName.doesNotContain=" + DEFAULT_DISPLAY_NAME);
    }

    @Test
    @Transactional
    void getAllPartiesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where email equals to
        defaultPartyFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllPartiesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where email in
        defaultPartyFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllPartiesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where email is not null
        defaultPartyFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllPartiesByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where email contains
        defaultPartyFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllPartiesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where email does not contain
        defaultPartyFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllPartiesByDobIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where dob equals to
        defaultPartyFiltering("dob.equals=" + DEFAULT_DOB, "dob.equals=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllPartiesByDobIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where dob in
        defaultPartyFiltering("dob.in=" + DEFAULT_DOB + "," + UPDATED_DOB, "dob.in=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllPartiesByDobIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where dob is not null
        defaultPartyFiltering("dob.specified=true", "dob.specified=false");
    }

    @Test
    @Transactional
    void getAllPartiesByDobIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where dob is greater than or equal to
        defaultPartyFiltering("dob.greaterThanOrEqual=" + DEFAULT_DOB, "dob.greaterThanOrEqual=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllPartiesByDobIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where dob is less than or equal to
        defaultPartyFiltering("dob.lessThanOrEqual=" + DEFAULT_DOB, "dob.lessThanOrEqual=" + SMALLER_DOB);
    }

    @Test
    @Transactional
    void getAllPartiesByDobIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where dob is less than
        defaultPartyFiltering("dob.lessThan=" + UPDATED_DOB, "dob.lessThan=" + DEFAULT_DOB);
    }

    @Test
    @Transactional
    void getAllPartiesByDobIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where dob is greater than
        defaultPartyFiltering("dob.greaterThan=" + SMALLER_DOB, "dob.greaterThan=" + DEFAULT_DOB);
    }

    @Test
    @Transactional
    void getAllPartiesByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where notes equals to
        defaultPartyFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllPartiesByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where notes in
        defaultPartyFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllPartiesByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where notes is not null
        defaultPartyFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    @Transactional
    void getAllPartiesByNotesContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where notes contains
        defaultPartyFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllPartiesByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where notes does not contain
        defaultPartyFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    @Transactional
    void getAllPartiesByMobileNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where mobileNumber equals to
        defaultPartyFiltering("mobileNumber.equals=" + DEFAULT_MOBILE_NUMBER, "mobileNumber.equals=" + UPDATED_MOBILE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPartiesByMobileNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where mobileNumber in
        defaultPartyFiltering(
            "mobileNumber.in=" + DEFAULT_MOBILE_NUMBER + "," + UPDATED_MOBILE_NUMBER,
            "mobileNumber.in=" + UPDATED_MOBILE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPartiesByMobileNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where mobileNumber is not null
        defaultPartyFiltering("mobileNumber.specified=true", "mobileNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPartiesByMobileNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where mobileNumber contains
        defaultPartyFiltering("mobileNumber.contains=" + DEFAULT_MOBILE_NUMBER, "mobileNumber.contains=" + UPDATED_MOBILE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPartiesByMobileNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where mobileNumber does not contain
        defaultPartyFiltering(
            "mobileNumber.doesNotContain=" + UPDATED_MOBILE_NUMBER,
            "mobileNumber.doesNotContain=" + DEFAULT_MOBILE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPartiesByEmployeeIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where employeeId equals to
        defaultPartyFiltering("employeeId.equals=" + DEFAULT_EMPLOYEE_ID, "employeeId.equals=" + UPDATED_EMPLOYEE_ID);
    }

    @Test
    @Transactional
    void getAllPartiesByEmployeeIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where employeeId in
        defaultPartyFiltering("employeeId.in=" + DEFAULT_EMPLOYEE_ID + "," + UPDATED_EMPLOYEE_ID, "employeeId.in=" + UPDATED_EMPLOYEE_ID);
    }

    @Test
    @Transactional
    void getAllPartiesByEmployeeIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where employeeId is not null
        defaultPartyFiltering("employeeId.specified=true", "employeeId.specified=false");
    }

    @Test
    @Transactional
    void getAllPartiesByEmployeeIdContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where employeeId contains
        defaultPartyFiltering("employeeId.contains=" + DEFAULT_EMPLOYEE_ID, "employeeId.contains=" + UPDATED_EMPLOYEE_ID);
    }

    @Test
    @Transactional
    void getAllPartiesByEmployeeIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where employeeId does not contain
        defaultPartyFiltering("employeeId.doesNotContain=" + UPDATED_EMPLOYEE_ID, "employeeId.doesNotContain=" + DEFAULT_EMPLOYEE_ID);
    }

    @Test
    @Transactional
    void getAllPartiesByLoginIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where login equals to
        defaultPartyFiltering("login.equals=" + DEFAULT_LOGIN, "login.equals=" + UPDATED_LOGIN);
    }

    @Test
    @Transactional
    void getAllPartiesByLoginIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where login in
        defaultPartyFiltering("login.in=" + DEFAULT_LOGIN + "," + UPDATED_LOGIN, "login.in=" + UPDATED_LOGIN);
    }

    @Test
    @Transactional
    void getAllPartiesByLoginIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where login is not null
        defaultPartyFiltering("login.specified=true", "login.specified=false");
    }

    @Test
    @Transactional
    void getAllPartiesByLoginContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where login contains
        defaultPartyFiltering("login.contains=" + DEFAULT_LOGIN, "login.contains=" + UPDATED_LOGIN);
    }

    @Test
    @Transactional
    void getAllPartiesByLoginNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        // Get all the partyList where login does not contain
        defaultPartyFiltering("login.doesNotContain=" + UPDATED_LOGIN, "login.doesNotContain=" + DEFAULT_LOGIN);
    }

    @Test
    @Transactional
    void getAllPartiesByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            partyRepository.saveAndFlush(party);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        party.setUser(user);
        partyRepository.saveAndFlush(party);
        Long userId = user.getId();
        // Get all the partyList where user equals to userId
        defaultPartyShouldBeFound("userId.equals=" + userId);

        // Get all the partyList where user equals to (userId + 1)
        defaultPartyShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllPartiesByStatusIsEqualToSomething() throws Exception {
        Status status;
        if (TestUtil.findAll(em, Status.class).isEmpty()) {
            partyRepository.saveAndFlush(party);
            status = StatusResourceIT.createEntity();
        } else {
            status = TestUtil.findAll(em, Status.class).get(0);
        }
        em.persist(status);
        em.flush();
        party.setStatus(status);
        partyRepository.saveAndFlush(party);
        Long statusId = status.getId();
        // Get all the partyList where status equals to statusId
        defaultPartyShouldBeFound("statusId.equals=" + statusId);

        // Get all the partyList where status equals to (statusId + 1)
        defaultPartyShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    @Test
    @Transactional
    void getAllPartiesByPartyTypeIsEqualToSomething() throws Exception {
        PartyType partyType;
        if (TestUtil.findAll(em, PartyType.class).isEmpty()) {
            partyRepository.saveAndFlush(party);
            partyType = PartyTypeResourceIT.createEntity();
        } else {
            partyType = TestUtil.findAll(em, PartyType.class).get(0);
        }
        em.persist(partyType);
        em.flush();
        party.setPartyType(partyType);
        partyRepository.saveAndFlush(party);
        Long partyTypeId = partyType.getId();
        // Get all the partyList where partyType equals to partyTypeId
        defaultPartyShouldBeFound("partyTypeId.equals=" + partyTypeId);

        // Get all the partyList where partyType equals to (partyTypeId + 1)
        defaultPartyShouldNotBeFound("partyTypeId.equals=" + (partyTypeId + 1));
    }

    private void defaultPartyFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPartyShouldBeFound(shouldBeFound);
        defaultPartyShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPartyShouldBeFound(String filter) throws Exception {
        restPartyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(party.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].mobileNumber").value(hasItem(DEFAULT_MOBILE_NUMBER)))
            .andExpect(jsonPath("$.[*].employeeId").value(hasItem(DEFAULT_EMPLOYEE_ID)))
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)));

        // Check, that the count call also returns 1
        restPartyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPartyShouldNotBeFound(String filter) throws Exception {
        restPartyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPartyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingParty() throws Exception {
        // Get the party
        restPartyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParty() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the party
        Party updatedParty = partyRepository.findById(party.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParty are not directly saved in db
        em.detach(updatedParty);
        updatedParty
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .displayName(UPDATED_DISPLAY_NAME)
            .email(UPDATED_EMAIL)
            .dob(UPDATED_DOB)
            .notes(UPDATED_NOTES)
            .mobileNumber(UPDATED_MOBILE_NUMBER)
            .employeeId(UPDATED_EMPLOYEE_ID)
            .login(UPDATED_LOGIN);

        restPartyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedParty.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedParty))
            )
            .andExpect(status().isOk());

        // Validate the Party in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPartyToMatchAllProperties(updatedParty);
    }

    @Test
    @Transactional
    void putNonExistingParty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        party.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartyMockMvc
            .perform(put(ENTITY_API_URL_ID, party.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(party)))
            .andExpect(status().isBadRequest());

        // Validate the Party in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        party.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(party))
            )
            .andExpect(status().isBadRequest());

        // Validate the Party in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        party.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(party)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Party in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartyWithPatch() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the party using partial update
        Party partialUpdatedParty = new Party();
        partialUpdatedParty.setId(party.getId());

        partialUpdatedParty.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).dob(UPDATED_DOB).login(UPDATED_LOGIN);

        restPartyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParty.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParty))
            )
            .andExpect(status().isOk());

        // Validate the Party in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedParty, party), getPersistedParty(party));
    }

    @Test
    @Transactional
    void fullUpdatePartyWithPatch() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the party using partial update
        Party partialUpdatedParty = new Party();
        partialUpdatedParty.setId(party.getId());

        partialUpdatedParty
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .displayName(UPDATED_DISPLAY_NAME)
            .email(UPDATED_EMAIL)
            .dob(UPDATED_DOB)
            .notes(UPDATED_NOTES)
            .mobileNumber(UPDATED_MOBILE_NUMBER)
            .employeeId(UPDATED_EMPLOYEE_ID)
            .login(UPDATED_LOGIN);

        restPartyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParty.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParty))
            )
            .andExpect(status().isOk());

        // Validate the Party in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartyUpdatableFieldsEquals(partialUpdatedParty, getPersistedParty(partialUpdatedParty));
    }

    @Test
    @Transactional
    void patchNonExistingParty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        party.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, party.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(party))
            )
            .andExpect(status().isBadRequest());

        // Validate the Party in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        party.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(party))
            )
            .andExpect(status().isBadRequest());

        // Validate the Party in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        party.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(party)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Party in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParty() throws Exception {
        // Initialize the database
        insertedParty = partyRepository.saveAndFlush(party);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the party
        restPartyMockMvc
            .perform(delete(ENTITY_API_URL_ID, party.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return partyRepository.count();
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

    protected Party getPersistedParty(Party party) {
        return partyRepository.findById(party.getId()).orElseThrow();
    }

    protected void assertPersistedPartyToMatchAllProperties(Party expectedParty) {
        assertPartyAllPropertiesEquals(expectedParty, getPersistedParty(expectedParty));
    }

    protected void assertPersistedPartyToMatchUpdatableProperties(Party expectedParty) {
        assertPartyAllUpdatablePropertiesEquals(expectedParty, getPersistedParty(expectedParty));
    }
}
