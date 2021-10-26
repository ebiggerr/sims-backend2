package com.ebiggerr.sims.domain.account;

import com.ebiggerr.sims.DTO.Account.AccountUpdateInput;
import com.ebiggerr.sims.DTO.Account.CreateAccountInput;
import com.ebiggerr.sims.domain.BaseEntity;
import com.ebiggerr.sims.enumeration.AccountStatus;
import com.ebiggerr.sims.enumeration.PostgreSQLEnumType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.*;

@Entity
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
@Table(name="account")
public class Account extends BaseEntity implements UserDetails {

    protected static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private String username;

    @JsonIgnore
    private String password;

    @Column(length = 128, name ="\"emailAddress\"")
    private String emailAddress;

    @Column(length = 64, name ="\"firstName\"")
    private String firstName;

    @Column(length = 64, name ="\"lastName\"")
    private String lastName;

    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"accountStatus\"")
    @Type( type = "pgsql_enum" )
    private AccountStatus accountStatus;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "account")
    private List<AccountRole> accountRoleSet;

    protected Account(){

    }

    private Account(/*UUID id,
                   boolean isDeleted,
                   LocalDateTime creationTime,
                   LocalDateTime lastModificationTime,*/
                   String username,
                   String password,
                   String emailAddress,
                   String firstName,
                   String lastName,
                   String remarks,
                   AccountStatus accountStatus,
                   List<AccountRole> accountRoleSet) {
        //super(id, isDeleted, creationTime, lastModificationTime);
        super();
        this.isDeleted = false;
        this.username = username;
        this.password = passwordEncoder.encode(password);
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.remarks = remarks;
        this.accountStatus = accountStatus;
        this.accountRoleSet = accountRoleSet;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // Initialize
        List<SimpleGrantedAuthority> authorities=new ArrayList<>();
        RoleDetails roleDetailsTemp;
        String roleName;

        for ( AccountRole accRole: this.accountRoleSet ) {
            roleDetailsTemp = accRole.getRoleDetails();
            roleName = roleDetailsTemp.getRoleName();
            authorities.add( new SimpleGrantedAuthority(roleName) );
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

    public static Account Create(@NotNull CreateAccountInput input){

        return new Account(
                input.username,
                input.password,
                input.emailAddress,
                input.firstName,
                input.lastName,
                "N/A", //remarks
                AccountStatus.PENDING,
                new LinkedList<>() // empty list

        );
    }

    public Account updateEntity(AccountUpdateInput accountUpdateInput){


        return new Account(); // TODO update
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
