package com.ebiggerr.sims.service.account;

import com.ebiggerr.sims.DTO.Account.CreateAccountInput;
import com.ebiggerr.sims.DTO.Result;
import com.ebiggerr.sims.DTO.Roles.UpdateRolesInput;
import com.ebiggerr.sims.domain.account.Account;
import com.ebiggerr.sims.domain.account.AccountRole;
import com.ebiggerr.sims.domain.account.RoleDetails;
import com.ebiggerr.sims.exception.CustomException;
import com.ebiggerr.sims.exception.RunTimeCustomException;
import com.ebiggerr.sims.repository.account.AccountRepo;
import com.ebiggerr.sims.repository.account.AccountRoleRepo;
import com.ebiggerr.sims.repository.account.RoleDetailsRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AccountService implements UserDetailsService {

    private final AccountRepo _accountRepo;
    private final RoleDetailsRepo _roleDetailsRepo;
    private final AccountRoleRepo _accountRoleRepo;

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

        // Will only retrieve the records of roles under an non-deleted account that are still active ( means, excluding those soft deleted roles )
        Optional<Account> acc1 = _accountRepo.retrieveOneUsingUsername( username );

        if( acc1.isPresent() ){
            return (Account)acc1.get();
        }
        else throw new UsernameNotFoundException("No user found");
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

    public Result assigningRolesToAnAccount(Account acc, UpdateRolesInput input, String username) throws RunTimeCustomException {

        Result result = new Result();
        result.status = false;
        String invalidRoles  = "";

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

            for( int i=0; i < roles.length; i++ ){
                invalidRoles =  invalid[i]  + " ";
            }
            result.message = "Some invalid roles that is not in the system cannot be assigned. : " + invalidRoles;
            result.status = false;
            result.arr = invalid;

            return result;
            //throw new RunTimeCustomException("There is some invalid roles that could not be assigned to the account.");
        }

        if (list.size() > 0 && roles.length == list.size() ) {
            _accountRoleRepo.saveAll(list);
            result.status = true;
            result.message = "Success";
        }
        else{
            result.status = false;
            result.message = "Something went wrong when adding roles.";

            return result;
        }

        return result;
    }

    public Result revokingRolesToAnAccount(Account acc, UpdateRolesInput input, String username){

        Result result = new Result();
        result.status = false;
        String invalidRoles  = "";

        String[] roles = RoleDetails.getRolesFromCommasSeparatedString(input);
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

        if ((roles.length > list.size()) && (list.size() >= 0)) {

            for( int i=0; i < roles.length; i++ ){
                invalidRoles =  invalid[i]  + " ";
            }
            result.message = "Some invalid roles that is not in the system cannot be revoked. : " + invalidRoles;
            result.status = false;
            result.arr = invalid;

            return result;
            //throw new RunTimeCustomException("There is some invalid roles that could not be assigned to the account.");
        }

        if (list.size() > 0 && roles.length == list.size() ) {


            for (AccountRole accRole: list) {

                //query those that is still active ( meaning excluding those soft deleted )
                Optional<AccountRole> query = _accountRoleRepo.findActiveOneUsingAccountIdAndRoleId(accRole.getAccountId(), accRole.getRoleId() );

                if (query.isPresent()){

                    //update the entity
                    AccountRole entity = query.get();
                    entity.SoftDelete();
                    _accountRoleRepo.save(entity);

                }
            }

            result.status = true;
            result.message = "Success";
        }
        else{
            result.status = false;
            result.message = "Something went wrong when revoking roles.";

            return result;
        }

        return result;
    }

}
