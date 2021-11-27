package com.ebiggerr.sims.domain.account;

import com.ebiggerr.sims.DTO.Roles.RoleInput;
import com.ebiggerr.sims.DTO.Roles.UpdateRoleDetailsInput;
import com.ebiggerr.sims.DTO.Roles.UpdateRolesInput;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="\"roleDetails\"")
public class RoleDetails{

    @Id
    @Column(name="\"roleId\"")
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

    protected RoleDetails(RoleInput input){
        this.creationTime = LocalDateTime.now();
        this.isDeleted = false;
        this.roleDescription = input.getRoleDescription();
        this.roleName = input.getRoleName();
    }

    public void updateRoleDetails(UpdateRoleDetailsInput input){

        this.roleName = input.getRoleName();
        this.roleDescription = input.getRoleDescription();
        this.lastModificationTime = LocalDateTime.now();

    }

    public void deleteRoleDetails(){
        this.isDeleted = true;
        this.lastModificationTime = LocalDateTime.now();
    }

    public static String[] getRolesFromCommasSeparatedString(UpdateRolesInput input){
        return input.roles.split(",");
    }

    public static RoleDetails createAnNewRole(RoleInput input){

        return new RoleDetails(input);

    }
}
