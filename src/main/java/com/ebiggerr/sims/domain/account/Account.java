package com.ebiggerr.sims.domain.account;

import com.ebiggerr.sims.domain.BaseEntity;
import com.ebiggerr.sims.enumeration.AccountStatus;
import com.ebiggerr.sims.enumeration.PostgreSQLEnumType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
@Table(name="account")
public class Account extends BaseEntity implements UserDetails {

    private String username;

    @JsonIgnore
    private String password;

    @Column(length = 128)
    private String emailAddress;

    @Column(length = 64)
    private String firstName;

    @Column(length = 64)
    private String lastName;

    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "accountstatus")
    @Type( type = "pgsql_enum" )
    private AccountStatus accountStatus;

    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name = "accountId", nullable = false, insertable = false, updatable = false)
    private Set<AccountRole> accountRoleSet;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> authorities=new ArrayList<>();

        RoleDetails roleDetailsTemp;

        for ( AccountRole accRole: this.accountRoleSet ) {
            roleDetailsTemp = accRole.getRoleDetails();
            String roleName = roleDetailsTemp.getRoleName();
            authorities.add( new SimpleGrantedAuthority(roleName));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getEmailAddress(){
        return this.emailAddress;
    }

    public String getRemarks(){
        return this.remarks;
    }

    public AccountStatus getAccountStatus(){
        return this.accountStatus;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
