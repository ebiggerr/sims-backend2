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

        Optional<Account> acc1 = _accountRepo.retrieveOneUsingUsername( username );

        if( acc1.isPresent() ){
            return (Account)acc1.get();
        }
        else throw new UsernameNotFoundException("No user found");
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

        String[] roles = RoleDetails.getRolesFromCommasSeparatedString(input);
        List<AccountRole> list = new LinkedList<>();
        String[] invalid = new String[roles.length];
        AtomicInteger index = new AtomicInteger(0);

        for (String role : roles) {
            Optional<RoleDetails> roleDetails = _roleDetailsRepo.getByRoleNameIs(role);

            roleDetails.ifPresentOrElse((details) -> {
                        list.add(AccountRole.assigningRolesToAnAccount(acc, details, username));
                    }
                    ,
                    () -> {
                        invalid[index.get()] = role;
                        index.getAndIncrement();
                    }
            );

        }

        if ((roles.length > list.size()) && (list.size() > 0)) {

            // return something meaningful

            throw new RunTimeCustomException("There is some invalid roles that could not be assigned to the account.");
        }

        if (list.size() > 0) {
            _accountRoleRepo.saveAll(list);
            return null;
        }

        return null;
    }

}
