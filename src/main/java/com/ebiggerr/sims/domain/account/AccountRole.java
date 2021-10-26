package com.ebiggerr.sims.domain.account;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@IdClass(accountIdRoleId.class)
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

    // Who grant this account with the roles
    private String username;

    protected AccountRole(){

    }

    private AccountRole(UUID accountId, String roleId, String username){
        this.accountId = accountId;
        this.roleId = roleId;
        this.creationTime = LocalDateTime.now();
        this.username = username;
        //Default
        this.isDeleted = false;

    }

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

    public String getRoleId(){
        return roleId;
    }

    public UUID getAccountId(){
        return accountId;
    }

    public String getAccountIdInString(){
        return accountId.toString();
    }

    public static AccountRole assigningRolesToAnAccount(Account acc, RoleDetails roleDetails, String username){

        return new AccountRole(acc.getId(),
                roleDetails.getRoleId(),
                username);

    }

    public void SoftDelete(){
        this.isDeleted = true;
    }
}
