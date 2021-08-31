package com.ebiggerr.sims.controller.account;

import com.ebiggerr.sims.DTO.API_RESPONSE;
import com.ebiggerr.sims.DTO.Account.AccountOutput;
import com.ebiggerr.sims.DTO.Account.UserName_Password_Input;
import com.ebiggerr.sims.DTO.token.TokenDto;
import com.ebiggerr.sims.config.jwt.Token_Provider;
import com.ebiggerr.sims.domain.account.Account;
import com.ebiggerr.sims.exception.CustomException;
import com.ebiggerr.sims.mapper.account.AccountMapper;
import com.ebiggerr.sims.service.account.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private final AccountService _accountService;
    private final AuthenticationManager _authenticationManager;

    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService,
                             AuthenticationManager authenticationManager
                             ){

        _accountService = accountService;
        _authenticationManager = authenticationManager;
    }

    /**
     * Endpoint for user to request token using username and password, the token is needed for user to have access to protected endpoints
     *
     * @param input User input of username and password
     * @return Token wrapped in container
     */
    @PostMapping(path = "/authenticate")
    public API_RESPONSE getToken(@RequestBody UserName_Password_Input input) {

        Authentication auth = null;

        try {

            auth = _authenticationManager.authenticate(new UsernamePasswordAuthenticationToken( input.getUsername(), input.getPassword() ));
            SecurityContextHolder.getContext().setAuthentication(auth);

            if( auth.isAuthenticated() ){

                String token = Token_Provider.generateTokenFromAuthentication(auth);
                TokenDto tokenDto = new TokenDto(token);

                return new API_RESPONSE().Success(tokenDto);
            }


        }catch (UsernameNotFoundException | CustomException e){

            if( e instanceof UsernameNotFoundException ){
                return new API_RESPONSE().Unauthorized();
            }
            else{
                logger.error( "Something went wrong when doing authentication. " + e.getMessage() );
                return new API_RESPONSE().Error();
            }
        }
        return new API_RESPONSE().Error();
    }


    @GetMapping(path = "/user" )
    public API_RESPONSE getAUserUsingUsername(@RequestBody UserName_Password_Input input){

        try {
            Account acc = (Account) _accountService.loadUserByUsername(input.getUsername());
            AccountOutput accDto = AccountMapper.INSTANCE.accountToAccountDto(acc);

            return new API_RESPONSE().Success(accDto);

        }catch (UsernameNotFoundException exception){
            return new API_RESPONSE().NotFound(exception.getMessage());
        }
    }
}
