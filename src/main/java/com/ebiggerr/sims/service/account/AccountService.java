package com.ebiggerr.sims.service.account;

import com.ebiggerr.sims.DTO.Account.CreateAccountInput;
import com.ebiggerr.sims.DTO.Roles.RoleInput;
import com.ebiggerr.sims.DTO.Result;
import com.ebiggerr.sims.DTO.Roles.UpdateRoleDetailsInput;
import com.ebiggerr.sims.DTO.Roles.UpdateRolesInput;
import com.ebiggerr.sims.config.jwt.Token_Provider;
import com.ebiggerr.sims.domain.account.Account;
import com.ebiggerr.sims.domain.account.AccountRole;
import com.ebiggerr.sims.domain.account.RoleDetails;
import com.ebiggerr.sims.exception.CustomException;
import com.ebiggerr.sims.exception.RunTimeCustomException;
import com.ebiggerr.sims.mapper.account.RoleMapper;
import com.ebiggerr.sims.repository.account.AccountRepo;
import com.ebiggerr.sims.repository.account.AccountRoleRepo;
import com.ebiggerr.sims.repository.account.RoleDetailsRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AccountService implements UserDetailsService {

    private final AccountRepo _accountRepo;
    private final RoleDetailsRepo _roleDetailsRepo;
    private final AccountRoleRepo _accountRoleRepo;

    private final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public AccountService(AccountRepo accountRepo, RoleDetailsRepo roleDetailsRepo, AccountRoleRepo accountRoleRepo){
        _accountRepo = accountRepo;
        _roleDetailsRepo = roleDetailsRepo;
        _accountRoleRepo = accountRoleRepo;
    }

    /**
     * Method overridden from UserDetails class to load an user/account entity by searching it inside database.
     *
     * @param username username of an account
     * @return UserDetails that can be casted to Account entity because Account implements the class
     * @throws UsernameNotFoundException when the username is not found in the database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // low performance - have to query twice

        // this region only for update the last login time
        Optional<Account> noNestedEntity = _accountRepo.retrieveOneUsingUsername_NoNested(username);
        if(noNestedEntity.isPresent()){
            updateLastLoginTime( noNestedEntity.get() );
        }
        else throw new UsernameNotFoundException("No user found");
        //region end

        // Will only retrieve the records of roles under an non-deleted account that are still active ( means, excluding those soft deleted roles )
        Optional<Account> acc1 = _accountRepo.retrieveOneUsingUsername( username );

        if( acc1.isPresent() ){
            return (Account)acc1.get();
        }
        else throw new UsernameNotFoundException("No user found");
    }

    public Account updateLastLoginTime(Account account){

        account.updateLastLogin();
        _accountRepo.save(account);
        return null;

    }

    public Account loadUserByUsername_NotLogin(String username) throws UsernameNotFoundException {

            // Will only retrieve the records of roles under an non-deleted account that are still active ( means, excluding those soft deleted roles )
            Optional<Account> acc1 = _accountRepo.retrieveOneUsingUsername(username);

            if (acc1.isPresent()) {
                return acc1.get();
            } else throw new UsernameNotFoundException("No user found");
    }

    public Account loadUserById(String id) throws UsernameNotFoundException{

        Optional<Account> acc1 = _accountRepo.retrieveOneUsingId( UUID.fromString(id) );

        if( acc1.isPresent() ){
            return acc1.get();
        }
        else throw new UsernameNotFoundException("No user found with this Id : " + id );
    }

    public boolean registerAnAccount(CreateAccountInput input) {

        try {
            Account acc = Account.Create(input);

            _accountRepo.save(acc);

        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Transactional
    public Result assigningRolesToAnAccount(String token, UpdateRolesInput input) throws RunTimeCustomException {

        String username; //username that made the request
        Result result = new Result();
        result.status = false;

        try{
            username = Token_Provider.getUsernameFromToken(token);
        }catch (CustomException e){
            return result;
        }

        if(username != null) {

            Account acc;
            String invalidRoles  = "";

            Optional<Account> optionalAccount = _accountRepo.retrieveOneUsingUsername_NoNested(input.username);

            if( optionalAccount.isPresent() ){
                acc = optionalAccount.get();
            }
            else{
                return result;
            }

            //roles from user input
            String[] roles = RoleDetails.getRolesFromCommasSeparatedString(input);
            //valid roles retrieved from the database based on the user input
            List<AccountRole> list = new LinkedList<>();
            String[] invalid = new String[roles.length];
            AtomicInteger index = new AtomicInteger(0);

            for (String role : roles) {
                // in case user input is in upper case. For example: "ADMIN"
                // capitalize -> "Admin"
                role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
                Optional<RoleDetails> roleDetails = _roleDetailsRepo.getByRoleNameIs(role);

                //final for the lambda expression later
                String finalRole = role;
                roleDetails.ifPresentOrElse((details) -> {
                            list.add(AccountRole.assigningRolesToAnAccount(acc, details, username));
                        }
                        ,
                        () -> {
                            invalid[index.get()] = finalRole;
                            index.getAndIncrement();
                        }
                );

            }

            // if there is more roles from the user input than the valid roles retrieved
            if ((roles.length > list.size()) && (list.size() >= 0)) {

                for (int i = 0; i < roles.length; i++) {
                    invalidRoles = invalid[i] + ", ";
                }
                result.message = "Some invalid roles that is not in the system cannot be assigned. : " + invalidRoles;
                result.arr = invalid;

                return result;
                //throw new RunTimeCustomException("There is some invalid roles that could not be assigned to the account.");
            }

            if (list.size() > 0 && roles.length == list.size()) {
                _accountRoleRepo.saveAll(list);
                result.status = true;
                result.message = "Success";

                logger.info("Admin with username : " + username + " updated the roles for account with username : " + input.username );
            } else {
                result.message = "Something went wrong when adding roles.";
            }
        }

        return result;
    }

    @Transactional
    public Result revokingRolesToAnAccount(String token, UpdateRolesInput input){

        String username; //username that made the request
        Result result = new Result();
        result.status = false;

        try{
            username = Token_Provider.getUsernameFromToken(token);
        }catch (CustomException e){
            return result;
        }

        if(username != null) {

            Account acc;

            Optional<Account> optionalAccount = _accountRepo.retrieveOneUsingUsername_NoNested(input.username);

            if( optionalAccount.isPresent() ){
                acc = optionalAccount.get();
            }
            else{
                return result;
            }

            String[] roles = RoleDetails.getRolesFromCommasSeparatedString(input);

            for (String role : roles){

                role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
                Optional<RoleDetails> roleDetails = _roleDetailsRepo.getByRoleNameIs(role);

                if( roleDetails.isPresent()){

                    Optional<AccountRole> accountRole = _accountRoleRepo.findActiveOneUsingAccountIdAndRoleId(acc.getId(), roleDetails.get().getRoleId() );

                    if( accountRole.isPresent()){
                        AccountRole accRole = accountRole.get();
                        accRole.SoftDelete();
                        _accountRoleRepo.save(accRole);
                        logger.info("Admin user with username : " + username + " revoked Role : " + roleDetails.get().getRoleName() + " of account with username : " + acc.getUsername() );
                    }
                    else{
                        return result;
                    }

                }
            }

            result.status = true;
            return result;
        }

        return result;
    }

    public boolean revokeAnAccount(String username, String adminUsername) throws UsernameNotFoundException{

        Optional<Account> acc = _accountRepo.retrieveOneUsingUsername_NoNested(username);

        if(acc.isPresent()){
            Account account = acc.get();
            account.softDelete(adminUsername); // will update the remark of the account
            _accountRepo.save(account);

            return true;
        }
        else{
            throw new UsernameNotFoundException("No user found");
        }


    }

    public boolean approveAnAccount(String username, String adminUsername) throws UsernameNotFoundException{

        Optional<Account> acc = _accountRepo.retrieveOneUsingUsername_NoNested(username);

        if(acc.isPresent()){
            Account account = acc.get();
            account.approveAccount(adminUsername); // will update the remark of the account
            _accountRepo.save(account);

            return true;
        }
        else{
            throw new UsernameNotFoundException("No user found");
        }


    }

    public RoleInput createAnNewRole(RoleInput input){

        RoleDetails entity = RoleDetails.createAnNewRole(input);

        try {
            _roleDetailsRepo.save(entity);
            return RoleMapper.INSTANCE.roleToRoleDTO(entity);

        }catch (Exception e) {
            return new RoleInput();
        }
    }

    public UpdateRoleDetailsInput updateARole(UpdateRoleDetailsInput input){

        Optional<RoleDetails> roleDetails = _roleDetailsRepo.getByRoleIdIs(input.roleId);

        if(roleDetails.isPresent()){

            RoleDetails entity = roleDetails.get();
            entity.updateRoleDetails(input);

            _roleDetailsRepo.save(entity);

            return RoleMapper.INSTANCE.roleToRoleDetailsUpdateDto(entity);
        }
        else{
            return new UpdateRoleDetailsInput();
        }

    }

    public boolean deleteARole(UpdateRoleDetailsInput input){

        Optional<RoleDetails> roleDetails = _roleDetailsRepo.getByRoleIdIs(input.roleId);

        if(roleDetails.isPresent()){

            RoleDetails entity = roleDetails.get();
            entity.deleteRoleDetails();

            _roleDetailsRepo.save(entity);

            return true;
        }


        return false;
    }
}
