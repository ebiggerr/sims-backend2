package com.ebiggerr.sims.repository.account;

import com.ebiggerr.sims.domain.account.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRoleRepo extends JpaRepository<AccountRole, UUID> {

    @Query("SELECT accRole FROM AccountRole accRole WHERE accRole.isDeleted=false AND accRole.accountId=:accountId AND accRole.roleId=:roleId")
    Optional<AccountRole> findActiveOneUsingAccountIdAndRoleId(UUID accountId, String roleId);
}
