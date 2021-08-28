package com.ebiggerr.sims.domain.account;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="account")
public class Account implements UserDetails {

    @Id
    private final UUID id = UUID.randomUUID();

    private LocalDateTime creationTime;

    private LocalDateTime lastModificationTime;

    private String username;

    private String password;

    private String emailAddress;

    private String remarks;

    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumns({@JoinColumn(name="accountid",referencedColumnName = "accountid")})
    private Set<AccountRole> accountRoleSet;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
