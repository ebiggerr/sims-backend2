package com.ebiggerr.sims.domain.account;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@IdClass(accountIdRoleId.class)
@Table(name ="accountRole")
public class AccountRole {

    @Id
    private UUID accountId;

    @Id
    private String roleId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roleId", nullable = false, insertable = false, updatable = false)
    private RoleDetails roleDetails;

    private LocalDateTime creationTime;

    private LocalDateTime lastModificationTime;

    private String username;

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public LocalDateTime getLastModificationTime() {
        return lastModificationTime;
    }

    public String getUsername() {
        return username;
    }

    public RoleDetails getRoleDetails() {
        return roleDetails;
    }
}
