package com.rogers.accountmanager.repository;

import com.rogers.accountmanager.domain.AccountsInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AccountsInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AccountsInfoRepository extends JpaRepository<AccountsInfo, Long> {
    Optional<AccountsInfo> findByEmail(String email);
}
