package com.ebiggerr.sims.service.account;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Account> acc1 = _accountRepo.retrieveOneUsingUsername( username );

        if( acc1.isPresent() ){
            return (Account)acc1.get(); //casting it
        }
        else throw new UsernameNotFoundException("No user found");
    }
}
