package com.ebiggerr.sims.repository.account;

import com.ebiggerr.sims.domain.account.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRoleRepo extends JpaRepository<AccountRole, UUID> {
}
