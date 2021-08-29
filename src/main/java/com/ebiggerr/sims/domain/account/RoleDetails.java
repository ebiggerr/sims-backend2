package com.ebiggerr.sims.domain.account;

import com.ebiggerr.sims.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="roleDetails")
public class RoleDetails extends BaseEntity {

    private String roleId;

    private String roleName;

    private String roleDescription;

    private LocalDateTime creationTime;

    private LocalDateTime lastModificationTime;

    public String getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void updateRoleDetails(RoleDetails roleDetailsInput){

        this.roleName = roleDetailsInput.roleName;
        this.roleDescription = roleDetailsInput.roleDescription;
        this.lastModificationTime = roleDetailsInput.lastModificationTime;

    }
}
