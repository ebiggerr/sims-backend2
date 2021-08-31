package com.ebiggerr.sims.repository.account;

import com.ebiggerr.sims.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepo extends JpaRepository<Account, UUID> {

    @Query("SELECT DISTINCT acc FROM Account acc JOIN FETCH acc.accountRoleSet roles JOIN FETCH roles.roleDetails details WHERE acc.id= :id")
    Optional<Account> retrieveOneUsingId(UUID id);

    @Query("SELECT DISTINCT acc FROM Account acc JOIN FETCH acc.accountRoleSet roles JOIN FETCH roles.roleDetails details WHERE acc.username= :username")
    Optional<Account> retrieveOneUsingUsername(String username);

}
