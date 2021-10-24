package com.ebiggerr.sims.repository.account;

import com.ebiggerr.sims.domain.account.RoleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleDetailsRepo extends JpaRepository<RoleDetails, UUID> {

    Optional<RoleDetails> getByRoleNameIs(String roleName);
}
