package com.rogers.accountmanager.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rogers.accountmanager.IntegrationTest;
import com.rogers.accountmanager.domain.AccountsInfo;
import com.rogers.accountmanager.repository.AccountsInfoRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AccountsInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AccountsInfoResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final Integer DEFAULT_POSTAL_CODE = 0;
    private static final Integer UPDATED_POSTAL_CODE = 1;

    private static final Integer DEFAULT_AGE = 1;
    private static final Integer UPDATED_AGE = 2;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_PLACE = "AAAAAAAAAA";
    private static final String UPDATED_PLACE = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AA";
    private static final String UPDATED_STATE = "BB";

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Integer DEFAULT_SECURITY_PIN = 4;
    private static final Integer UPDATED_SECURITY_PIN = 5;

    private static final String ENTITY_API_URL = "/api/accounts-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AccountsInfoRepository accountsInfoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAccountsInfoMockMvc;

    private AccountsInfo accountsInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountsInfo createEntity(EntityManager em) {
        AccountsInfo accountsInfo = new AccountsInfo()
            .name(DEFAULT_NAME)
            .email(DEFAULT_EMAIL)
            //            .country(DEFAULT_COUNTRY)
            .postalCode(DEFAULT_POSTAL_CODE)
            .age(DEFAULT_AGE)
            //            .status(DEFAULT_STATUS)
            .place(DEFAULT_PLACE)
            .state(DEFAULT_STATE)
            .longitude(DEFAULT_LONGITUDE)
            .latitude(DEFAULT_LATITUDE)
            .securityPin(DEFAULT_SECURITY_PIN);
        return accountsInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountsInfo createUpdatedEntity(EntityManager em) {
        AccountsInfo accountsInfo = new AccountsInfo()
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            //            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .age(UPDATED_AGE)
            //            .status(UPDATED_STATUS)
            .place(UPDATED_PLACE)
            .state(UPDATED_STATE)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .securityPin(UPDATED_SECURITY_PIN);
        return accountsInfo;
    }

    @BeforeEach
    public void initTest() {
        accountsInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createAccountsInfo() throws Exception {
        int databaseSizeBeforeCreate = accountsInfoRepository.findAll().size();
        // Create the AccountsInfo
        restAccountsInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountsInfo)))
            .andExpect(status().isCreated());

        // Validate the AccountsInfo in the database
        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeCreate + 1);
        AccountsInfo testAccountsInfo = accountsInfoList.get(accountsInfoList.size() - 1);
        assertThat(testAccountsInfo.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAccountsInfo.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testAccountsInfo.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testAccountsInfo.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testAccountsInfo.getAge()).isEqualTo(DEFAULT_AGE);
        assertThat(testAccountsInfo.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testAccountsInfo.getPlace()).isEqualTo(DEFAULT_PLACE);
        assertThat(testAccountsInfo.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testAccountsInfo.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testAccountsInfo.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testAccountsInfo.getSecurityPin()).isEqualTo(DEFAULT_SECURITY_PIN);
    }

    @Test
    @Transactional
    void createAccountsInfoWithExistingId() throws Exception {
        // Create the AccountsInfo with an existing ID
        accountsInfo.setId(1L);

        int databaseSizeBeforeCreate = accountsInfoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAccountsInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountsInfo)))
            .andExpect(status().isBadRequest());

        // Validate the AccountsInfo in the database
        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = accountsInfoRepository.findAll().size();
        // set the field null
        accountsInfo.setName(null);

        // Create the AccountsInfo, which fails.

        restAccountsInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountsInfo)))
            .andExpect(status().isBadRequest());

        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = accountsInfoRepository.findAll().size();
        // set the field null
        accountsInfo.setEmail(null);

        // Create the AccountsInfo, which fails.

        restAccountsInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountsInfo)))
            .andExpect(status().isBadRequest());

        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryIsRequired() throws Exception {
        int databaseSizeBeforeTest = accountsInfoRepository.findAll().size();
        // set the field null
        accountsInfo.setCountry(null);

        // Create the AccountsInfo, which fails.

        restAccountsInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountsInfo)))
            .andExpect(status().isBadRequest());

        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPostalCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = accountsInfoRepository.findAll().size();
        // set the field null
        accountsInfo.setPostalCode(null);

        // Create the AccountsInfo, which fails.

        restAccountsInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountsInfo)))
            .andExpect(status().isBadRequest());

        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = accountsInfoRepository.findAll().size();
        // set the field null
        accountsInfo.setStatus(null);

        // Create the AccountsInfo, which fails.

        restAccountsInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountsInfo)))
            .andExpect(status().isBadRequest());

        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAccountsInfos() throws Exception {
        // Initialize the database
        accountsInfoRepository.saveAndFlush(accountsInfo);

        // Get all the accountsInfoList
        restAccountsInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountsInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].place").value(hasItem(DEFAULT_PLACE)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].securityPin").value(hasItem(DEFAULT_SECURITY_PIN)));
    }

    @Test
    @Transactional
    void getAccountsInfo() throws Exception {
        // Initialize the database
        accountsInfoRepository.saveAndFlush(accountsInfo);

        // Get the accountsInfo
        restAccountsInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, accountsInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(accountsInfo.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY))
            .andExpect(jsonPath("$.postalCode").value(DEFAULT_POSTAL_CODE))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.place").value(DEFAULT_PLACE))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.securityPin").value(DEFAULT_SECURITY_PIN));
    }

    @Test
    @Transactional
    void getNonExistingAccountsInfo() throws Exception {
        // Get the accountsInfo
        restAccountsInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAccountsInfo() throws Exception {
        // Initialize the database
        accountsInfoRepository.saveAndFlush(accountsInfo);

        int databaseSizeBeforeUpdate = accountsInfoRepository.findAll().size();

        // Update the accountsInfo
        AccountsInfo updatedAccountsInfo = accountsInfoRepository.findById(accountsInfo.getId()).get();
        // Disconnect from session so that the updates on updatedAccountsInfo are not directly saved in db
        em.detach(updatedAccountsInfo);
        updatedAccountsInfo
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            //            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .age(UPDATED_AGE)
            //            .status(UPDATED_STATUS)
            .place(UPDATED_PLACE)
            .state(UPDATED_STATE)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .securityPin(UPDATED_SECURITY_PIN);

        restAccountsInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAccountsInfo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAccountsInfo))
            )
            .andExpect(status().isOk());

        // Validate the AccountsInfo in the database
        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeUpdate);
        AccountsInfo testAccountsInfo = accountsInfoList.get(accountsInfoList.size() - 1);
        assertThat(testAccountsInfo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAccountsInfo.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAccountsInfo.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testAccountsInfo.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testAccountsInfo.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testAccountsInfo.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAccountsInfo.getPlace()).isEqualTo(UPDATED_PLACE);
        assertThat(testAccountsInfo.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testAccountsInfo.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testAccountsInfo.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testAccountsInfo.getSecurityPin()).isEqualTo(UPDATED_SECURITY_PIN);
    }

    @Test
    @Transactional
    void putNonExistingAccountsInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountsInfoRepository.findAll().size();
        accountsInfo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountsInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accountsInfo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountsInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountsInfo in the database
        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAccountsInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountsInfoRepository.findAll().size();
        accountsInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountsInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountsInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountsInfo in the database
        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAccountsInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountsInfoRepository.findAll().size();
        accountsInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountsInfoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountsInfo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountsInfo in the database
        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAccountsInfoWithPatch() throws Exception {
        // Initialize the database
        accountsInfoRepository.saveAndFlush(accountsInfo);

        int databaseSizeBeforeUpdate = accountsInfoRepository.findAll().size();

        // Update the accountsInfo using partial update
        AccountsInfo partialUpdatedAccountsInfo = new AccountsInfo();
        partialUpdatedAccountsInfo.setId(accountsInfo.getId());

        partialUpdatedAccountsInfo
            .email(UPDATED_EMAIL)
            //            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            //            .status(UPDATED_STATUS)
            .state(UPDATED_STATE)
            .latitude(UPDATED_LATITUDE);

        restAccountsInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountsInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAccountsInfo))
            )
            .andExpect(status().isOk());

        // Validate the AccountsInfo in the database
        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeUpdate);
        AccountsInfo testAccountsInfo = accountsInfoList.get(accountsInfoList.size() - 1);
        assertThat(testAccountsInfo.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAccountsInfo.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAccountsInfo.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testAccountsInfo.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testAccountsInfo.getAge()).isEqualTo(DEFAULT_AGE);
        assertThat(testAccountsInfo.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAccountsInfo.getPlace()).isEqualTo(DEFAULT_PLACE);
        assertThat(testAccountsInfo.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testAccountsInfo.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testAccountsInfo.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testAccountsInfo.getSecurityPin()).isEqualTo(DEFAULT_SECURITY_PIN);
    }

    @Test
    @Transactional
    void fullUpdateAccountsInfoWithPatch() throws Exception {
        // Initialize the database
        accountsInfoRepository.saveAndFlush(accountsInfo);

        int databaseSizeBeforeUpdate = accountsInfoRepository.findAll().size();

        // Update the accountsInfo using partial update
        AccountsInfo partialUpdatedAccountsInfo = new AccountsInfo();
        partialUpdatedAccountsInfo.setId(accountsInfo.getId());

        partialUpdatedAccountsInfo
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            //            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .age(UPDATED_AGE)
            //            .status(UPDATED_STATUS)
            .place(UPDATED_PLACE)
            .state(UPDATED_STATE)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .securityPin(UPDATED_SECURITY_PIN);

        restAccountsInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountsInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAccountsInfo))
            )
            .andExpect(status().isOk());

        // Validate the AccountsInfo in the database
        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeUpdate);
        AccountsInfo testAccountsInfo = accountsInfoList.get(accountsInfoList.size() - 1);
        assertThat(testAccountsInfo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAccountsInfo.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAccountsInfo.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testAccountsInfo.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testAccountsInfo.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testAccountsInfo.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAccountsInfo.getPlace()).isEqualTo(UPDATED_PLACE);
        assertThat(testAccountsInfo.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testAccountsInfo.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testAccountsInfo.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testAccountsInfo.getSecurityPin()).isEqualTo(UPDATED_SECURITY_PIN);
    }

    @Test
    @Transactional
    void patchNonExistingAccountsInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountsInfoRepository.findAll().size();
        accountsInfo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountsInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, accountsInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountsInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountsInfo in the database
        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAccountsInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountsInfoRepository.findAll().size();
        accountsInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountsInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountsInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountsInfo in the database
        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAccountsInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountsInfoRepository.findAll().size();
        accountsInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountsInfoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(accountsInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountsInfo in the database
        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAccountsInfo() throws Exception {
        // Initialize the database
        accountsInfoRepository.saveAndFlush(accountsInfo);

        int databaseSizeBeforeDelete = accountsInfoRepository.findAll().size();

        // Delete the accountsInfo
        restAccountsInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, accountsInfo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AccountsInfo> accountsInfoList = accountsInfoRepository.findAll();
        assertThat(accountsInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
