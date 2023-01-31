package com.rogers.accountmanager.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rogers.accountmanager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccountsInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccountsInfo.class);
        AccountsInfo accountsInfo1 = new AccountsInfo();
        accountsInfo1.setId(1L);
        AccountsInfo accountsInfo2 = new AccountsInfo();
        accountsInfo2.setId(accountsInfo1.getId());
        assertThat(accountsInfo1).isEqualTo(accountsInfo2);
        accountsInfo2.setId(2L);
        assertThat(accountsInfo1).isNotEqualTo(accountsInfo2);
        accountsInfo1.setId(null);
        assertThat(accountsInfo1).isNotEqualTo(accountsInfo2);
    }
}
