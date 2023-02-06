package com.rogers.accountmanager.web.rest;

import static java.util.stream.Collectors.summingInt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rogers.accountmanager.domain.AccountsInfo;
import com.rogers.accountmanager.repository.AccountsInfoRepository;
import com.rogers.accountmanager.service.dto.CountOfUsersGroupedByStateAndPlaceDTO;
import com.rogers.accountmanager.web.rest.errors.BadRequestAlertException;
import com.sun.corba.se.spi.ior.ObjectKey;
import io.undertow.security.idm.Account;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.MethodNotAllowedException;
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

        // Calling place info from zippopoteam
        String apiUrl = "https://api.zippopotam.us/" + accountsInfo.getCountry() + "/" + accountsInfo.getPostalCode();
        ObjectMapper mapper = new ObjectMapper();
        // set state, Longitude, LAtitude and placeName by setPlace method
        setPlace(accountsInfo, apiUrl, mapper);

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
        @PathVariable(value = "id", required = false) final String id,
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
        // Account status must be active to edit
        if (accountsInfoRepository.findById(id).get().getStatus() == AccountsInfo.Status.ACTIVE) {
            // if address change requires to call the zippo api to retrieve new info
            String apiUrl = "https://api.zippopotam.us/" + accountsInfo.getCountry() + "/" + accountsInfo.getPostalCode();
            ObjectMapper mapper = new ObjectMapper();
            setPlace(accountsInfo, apiUrl, mapper);

            AccountsInfo result = accountsInfoRepository.save(accountsInfo);
            return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, accountsInfo.getId().toString()))
                .body(result);
        } else throw new BadRequestAlertException("Inactive Status", ENTITY_NAME, "InactiveStatus");
    }

    //  this method set the place infos retrived from zippo api
    private void setPlace(@RequestBody @Valid AccountsInfo accountsInfo, String apiUrl, ObjectMapper mapper) {
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
    public ResponseEntity<AccountsInfo> getAccountsInfo(@PathVariable String id) {
        log.debug("REST request to get AccountsInfo : {}", id);
        Optional<AccountsInfo> accountsInfo = accountsInfoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(accountsInfo);
    }

    /**
     * {@code GET  /accounts-infos/:id} : get the "id" accountsInfo.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accountsInfo, or with status {@code 404 (Not Found)}.
     */
    // finds by Id or Email
    @GetMapping("/accounts-infos-find")
    public ResponseEntity<AccountsInfo> getAccountsInfo(@RequestBody Map<String, String> searchBy) throws Exception {
        log.debug("REST request to get AccountsInfo : {}", searchBy.get("email"));

        Optional<AccountsInfo> accountsInfo = Optional.empty();
        if (searchBy.get("id").equals("") && searchBy.get("email").equals("")) throw new NullPointerException(
            "Processing fail. Got a null response"
        );
        if (!searchBy.get("id").equals("")) accountsInfo = accountsInfoRepository.findById(searchBy.get("id")); else accountsInfo =
            accountsInfoRepository.findByEmail(searchBy.get("email"));
        return ResponseUtil.wrapOrNotFound(accountsInfo);
    }

    /**
     404 (Not Found)}.
     */

    /**
     * {@code DELETE  /accounts-infos/:id} : delete the "id" accountsInfo.
     *
     * @param id the id of the accountsInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/accounts-infos/{id}")
    public ResponseEntity<Void> deleteAccountsInfo(@PathVariable String id, @RequestParam(required = true) Integer pin) {
        log.debug("REST request to delete AccountsInfo : {} with pin {}", id, pin);
        // execute the deletion logic if the object is not null and both the security pin and the status match the required conditions
        if (id.equals("") || pin == null) throw new NullPointerException();
        AccountsInfo accountsInfo = accountsInfoRepository.findById(id).orElse(null);
        if (!accountsInfo.getStatus().equals(AccountsInfo.Status.INACTIVE)) throw new BadRequestAlertException(
            "Invalid status",
            ENTITY_NAME,
            "Valid"
        );
        if (
            accountsInfo.getSecurityPin().equals(pin) && accountsInfo.getStatus().equals(AccountsInfo.Status.INACTIVE)
        ) accountsInfoRepository.deleteById(id);

        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/accounts-infos-retrieve")
    public ResponseEntity<Map<String, Object>> getGroupedAccounts() {
        List<AccountsInfo> accounts = accountsInfoRepository.findAll();

        Map<AccountsInfo.Country, Map<String, Map<String, Integer>>> groupedByCountryAndStateAndPlace = accounts
            .stream()
            .collect(
                Collectors.groupingBy(
                    AccountsInfo::getCountry,
                    Collectors.groupingBy(
                        AccountsInfo::getState,
                        Collectors.groupingBy(AccountsInfo::getPlace, Collectors.summingInt(a -> 1))
                    )
                )
            );

        Map<String, Object> response = new HashMap<>();
        groupedByCountryAndStateAndPlace.forEach(
            (country, stateAndPlaceCounts) -> {
                Map<String, Object> states = new HashMap<>();
                final int[] totalCount = { 0 };
                stateAndPlaceCounts.forEach(
                    (state, placeCounts) -> {
                        Map<String, Integer> places = new HashMap<>();
                        final int[] stateCount = { 0 };
                        placeCounts.forEach(
                            (place, count) -> {
                                places.put(place, count);
                                stateCount[0] += count;
                            }
                        );
                        Map<String, Object> placesInfo = new HashMap<>();
                        placesInfo.put("count", stateCount[0]);
                        placesInfo.put("places", places);

                        states.put(state, placesInfo);
                        totalCount[0] += stateCount[0];
                    }
                );
                Map<String, Object> countryInfo = new HashMap<>();
                countryInfo.put("count", totalCount);
                countryInfo.put("states", states);

                response.put(country.toString(), countryInfo);
            }
        );

        return ResponseEntity.ok(response);
    }

    private static String retrieveDataFromAPI(String apiUrl) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiUrl, String.class);
    }
}
