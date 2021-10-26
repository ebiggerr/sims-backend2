package com.ebiggerr.sims.domain.account;

import com.ebiggerr.sims.DTO.Roles.UpdateRolesInput;

import javax.management.relation.Role;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="\"roleDetails\"")
public class RoleDetails{

    @Id
    @Column(name="id")
    private String id;

    @Column(name ="\"roleName\"")
    private String roleName;

    @Column(name ="\"roleDescription\"")
    private String roleDescription;

    @Column(name ="\"creationTime\"")
    private LocalDateTime creationTime;

    @Column(name ="\"lastModificationTime\"")
    private LocalDateTime lastModificationTime;

    @Column(name ="\"isDeleted\"")
    private boolean isDeleted;

    public String getRoleId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    protected RoleDetails(){
        this.creationTime = LocalDateTime.now();
        this.isDeleted = false;
    }

    public void updateRoleDetails(RoleDetails roleDetailsInput){

        this.roleName = roleDetailsInput.roleName;
        this.roleDescription = roleDetailsInput.roleDescription;
        this.lastModificationTime = LocalDateTime.now();

    }

    public static String[] getRolesFromCommasSeparatedString(UpdateRolesInput input){
        return input.roles.split(",");
    }
}
