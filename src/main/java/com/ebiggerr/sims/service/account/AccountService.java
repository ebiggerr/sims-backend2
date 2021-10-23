package com.ebiggerr.sims.service.account;

import com.ebiggerr.sims.DTO.Account.CreateAccountInput;
import com.ebiggerr.sims.domain.account.Account;
import com.ebiggerr.sims.repository.account.AccountRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {

    private final AccountRepo _accountRepo;

    public AccountService(AccountRepo accountRepo){
        _accountRepo = accountRepo;
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
}
