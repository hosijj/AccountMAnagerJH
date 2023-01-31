package com.rogers.accountmanager.web.rest;

import com.rogers.accountmanager.domain.AccountsInfo;
import com.rogers.accountmanager.repository.AccountsInfoRepository;
import com.rogers.accountmanager.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<AccountsInfo> createAccountsInfo(@Valid @RequestBody AccountsInfo accountsInfo) throws URISyntaxException {
        log.debug("REST request to save AccountsInfo : {}", accountsInfo);
        if (accountsInfo.getId() != null) {
            throw new BadRequestAlertException("A new accountsInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AccountsInfo result = accountsInfoRepository.save(accountsInfo);
        return ResponseEntity
            .created(new URI("/api/accounts-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /accounts-infos/:id} : Updates an existing accountsInfo.
     *
     * @param id the id of the accountsInfo to save.
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
     * @param id the id of the accountsInfo to save.
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
     * {@code DELETE  /accounts-infos/:id} : delete the "id" accountsInfo.
     *
     * @param id the id of the accountsInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/accounts-infos/{id}")
    public ResponseEntity<Void> deleteAccountsInfo(@PathVariable Long id) {
        log.debug("REST request to delete AccountsInfo : {}", id);
        accountsInfoRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}