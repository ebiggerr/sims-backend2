package com.ebiggerr.sims.domain.account;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
//@IdClass(accountIdRoleId.class)
@Table(name ="\"accountRole\"")
public class AccountRole implements Serializable {

    @Id
    @Column(name ="\"accountId\"")
    private UUID accountId;

    @Id
    @Column(name ="\"roleId\"")
    private String roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"accountId\"",referencedColumnName="id" ,nullable = false,insertable = false, updatable = false)
    private Account account;

    @OneToOne
    @JoinColumn( referencedColumnName ="id", name = "\"roleId\"", nullable = false,insertable = false, updatable = false)
    private RoleDetails roleDetails;

    @Column(name ="\"creationTime\"")
    private LocalDateTime creationTime;

    @Column(name ="\"lastModificationTime\"")
    private LocalDateTime lastModificationTime;

    @Column(name ="\"isDeleted\"")
    private boolean isDeleted;

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
