package com.rogers.accountmanager.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rogers.accountmanager.domain.AccountsInfo;
import com.rogers.accountmanager.repository.AccountsInfoRepository;
import com.rogers.accountmanager.service.dto.CountOfUsersGroupedByStateAndPlaceDTO;
import com.rogers.accountmanager.web.rest.errors.BadRequestAlertException;
import com.sun.corba.se.spi.ior.ObjectKey;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.ws.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.rogers.accountmanager.domain.AccountsInfo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AccountsInfoResource {

    private final Logger log = LoggerFactory.getLogger(AccountsInfoResource.class);

    private static final String ENTITY_NAME = "accountsInfo";
    private RestTemplate restTemplate;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AccountsInfoRepository accountsInfoRepository;

    public AccountsInfoResource(AccountsInfoRepository accountsInfoRepository) {
        this.accountsInfoRepository = accountsInfoRepository;
    }

    /**
     * {@code POST  /accounts-infos} : Create a new accountsInfo.
     *
     * @param accountsInfo the accountsInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new accountsInfo, or with status {@code 400 (Bad Request)} if the accountsInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/accounts-infos")
    public ResponseEntity<String> createAccountsInfo(@Valid @RequestBody AccountsInfo accountsInfo)
        throws URISyntaxException, JsonProcessingException {
        accountsInfo.setStatus(AccountsInfo.Status.ACTIVE);

        log.debug("REST request to save AccountsInfo : {}", accountsInfo);
        if (accountsInfo.getId() != null) {
            throw new BadRequestAlertException("A new accountsInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }

        String apiUrl = "https://api.zippopotam.us/" + accountsInfo.getCountry() + "/" + accountsInfo.getPostalCode();
        System.out.println(retrieveDataFromAPI(apiUrl));
        //        String response = retrieveDataFromAPI(apiUrl);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> responseMap = mapper.readValue(retrieveDataFromAPI(apiUrl), Map.class);
            List<Map<String, Object>> places = (List<Map<String, Object>>) responseMap.get("places");
            String placeName = (String) places.get(0).get("place name");
            String state = (String) places.get(0).get("state abbreviation");
            Double longitude = Double.parseDouble((String) places.get(0).get("longitude"));
            Double latitude = Double.parseDouble((String) places.get(0).get("latitude"));
            accountsInfo.setPlace(placeName);
            accountsInfo.setState(state);
            accountsInfo.setLongitude(longitude);
            accountsInfo.setLatitude(latitude);
        } catch (IOException e) {
            // handle the error
            log.error("Error parsing response", e);
        }
        AccountsInfo result = accountsInfoRepository.save(accountsInfo);
        return ResponseEntity
            .created(new URI("/api/accounts-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(" AccountId: " + result.getId() + " Status: " + result.getStatus() + " SecurityPin: " + result.getSecurityPin());
    }

    /**
     * {@code PUT  /accounts-infos/:id} : Updates an existing accountsInfo.
     *
     * @param id           the id of the accountsInfo to save.
     * @param accountsInfo the accountsInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountsInfo,
     * or with status {@code 400 (Bad Request)} if the accountsInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the accountsInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/accounts-infos/{id}")
    public ResponseEntity<AccountsInfo> updateAccountsInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AccountsInfo accountsInfo
    ) throws URISyntaxException {
        log.debug("REST request to update AccountsInfo : {}, {}", id, accountsInfo);
        if (accountsInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountsInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountsInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AccountsInfo result = accountsInfoRepository.save(accountsInfo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, accountsInfo.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /accounts-infos/:id} : Partial updates given fields of an existing accountsInfo, field will ignore if it is null
     *
     * @param id           the id of the accountsInfo to save.
     * @param accountsInfo the accountsInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountsInfo,
     * or with status {@code 400 (Bad Request)} if the accountsInfo is not valid,
     * or with status {@code 404 (Not Found)} if the accountsInfo is not found,
     * or with status {@code 500 (Internal Server Error)} if the accountsInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/accounts-infos/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<AccountsInfo> partialUpdateAccountsInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AccountsInfo accountsInfo
    ) throws URISyntaxException {
        log.debug("REST request to partial update AccountsInfo partially : {}, {}", id, accountsInfo);
        if (accountsInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountsInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountsInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AccountsInfo> result = accountsInfoRepository
            .findById(accountsInfo.getId())
            .map(
                existingAccountsInfo -> {
                    if (accountsInfo.getName() != null) {
                        existingAccountsInfo.setName(accountsInfo.getName());
                    }
                    if (accountsInfo.getEmail() != null) {
                        existingAccountsInfo.setEmail(accountsInfo.getEmail());
                    }
                    if (accountsInfo.getCountry() != null) {
                        existingAccountsInfo.setCountry(accountsInfo.getCountry());
                    }
                    if (accountsInfo.getPostalCode() != null) {
                        existingAccountsInfo.setPostalCode(accountsInfo.getPostalCode());
                    }
                    if (accountsInfo.getAge() != null) {
                        existingAccountsInfo.setAge(accountsInfo.getAge());
                    }
                    if (accountsInfo.getStatus() != null) {
                        existingAccountsInfo.setStatus(accountsInfo.getStatus());
                    }
                    if (accountsInfo.getPlace() != null) {
                        existingAccountsInfo.setPlace(accountsInfo.getPlace());
                    }
                    if (accountsInfo.getState() != null) {
                        existingAccountsInfo.setState(accountsInfo.getState());
                    }
                    if (accountsInfo.getLongitude() != null) {
                        existingAccountsInfo.setLongitude(accountsInfo.getLongitude());
                    }
                    if (accountsInfo.getLatitude() != null) {
                        existingAccountsInfo.setLatitude(accountsInfo.getLatitude());
                    }
                    if (accountsInfo.getSecurityPin() != null) {
                        existingAccountsInfo.setSecurityPin(accountsInfo.getSecurityPin());
                    }

                    return existingAccountsInfo;
                }
            )
            .map(accountsInfoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, accountsInfo.getId().toString())
        );
    }

    /**
     * {@code GET  /accounts-infos} : get all the accountsInfos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of accountsInfos in body.
     */
    @GetMapping("/accounts-infos")
    public List<AccountsInfo> getAllAccountsInfos() {
        log.debug("REST request to get all AccountsInfos");
        return accountsInfoRepository.findAll();
    }

    /**
     * {@code GET  /accounts-infos/:id} : get the "id" accountsInfo.
     *
     * @param id the id of the accountsInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accountsInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/accounts-infos/{id}")
    public ResponseEntity<AccountsInfo> getAccountsInfo(@PathVariable Long id) {
        log.debug("REST request to get AccountsInfo : {}", id);
        Optional<AccountsInfo> accountsInfo = accountsInfoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(accountsInfo);
    }

    /**
     * {@code GET  /accounts-infos/:id} : get the "id" accountsInfo.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accountsInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/accounts-infos-find")
    public ResponseEntity<AccountsInfo> getAccountsInfo(@RequestBody Map<String, String> searchBy) throws Exception {
        log.debug("REST request to get AccountsInfo : {}", searchBy.get("email"));

        Optional<AccountsInfo> accountsInfo = Optional.empty();
        if (searchBy.get("id") == null || searchBy.get("email") == null) throw new NullPointerException(
            "Processing fail. Got a null response"
        );
        if (!searchBy.get("id").equals("")) accountsInfo =
            accountsInfoRepository.findById(Long.valueOf(searchBy.get("id"))); else accountsInfo =
            accountsInfoRepository.findByEmail(searchBy.get("email"));
        return ResponseUtil.wrapOrNotFound(accountsInfo);
    }

    /**
     404 (Not Found)}.
     */
    /*  @GetMapping("/accounts-infos-count")
    public ResponseEntity<List<CountOfUsersGroupedByStateAndPlaceDTO>> getCountOfUsersGroupedByStateAndPlace() {
        List<Object[]> results = accountsInfoRepository.getCountByCountryAndState();
        List<CountOfUsersGroupedByStateAndPlaceDTO> dtos = results.stream()
            .map(result -> new CountOfUsersGroupedByStateAndPlaceDTO((String) result[0], (String) result[1], (Long) result[2]))
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }*/

    /**
     * {@code DELETE  /accounts-infos/:id} : delete the "id" accountsInfo.
     *
     * @param id the id of the accountsInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/accounts-infos/{id}")
    public ResponseEntity<Void> deleteAccountsInfo(@PathVariable Long id, @RequestParam(required = true) Integer pin) {
        log.debug("REST request to delete AccountsInfo : {} with pin {}", id, pin);
        if (
            accountsInfoRepository.findById(id).get().getSecurityPin() == pin &&
            accountsInfoRepository.findById(id).get().getStatus().equals(AccountsInfo.Status.INACTIVE)
        ) accountsInfoRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    private static String retrieveDataFromAPI(String apiUrl) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiUrl, String.class);
    }
}
