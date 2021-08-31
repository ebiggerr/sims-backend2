package com.ebiggerr.sims.domain.account;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class accountIdRoleId implements Serializable {

    private UUID accountId;

    private String roleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        accountIdRoleId that = (accountIdRoleId) o;
        return accountId.equals(that.accountId) &&
                roleId.equals(that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, roleId);
    }
}
