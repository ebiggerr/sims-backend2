package com.ebiggerr.sims.controller.account;

import com.ebiggerr.sims.DTO.API_RESPONSE;
import com.ebiggerr.sims.DTO.Account.AccountOutput;
import com.ebiggerr.sims.DTO.Account.CreateAccountInput;
import com.ebiggerr.sims.DTO.Account.GetAccountInput;
import com.ebiggerr.sims.DTO.Account.UserName_Password_Input;
import com.ebiggerr.sims.DTO.Result;
import com.ebiggerr.sims.DTO.Roles.UpdateRolesInput;
import com.ebiggerr.sims.DTO.token.TokenDto;
import com.ebiggerr.sims.config.jwt.Token_Provider;
import com.ebiggerr.sims.domain.account.Account;
import com.ebiggerr.sims.exception.CustomException;
import com.ebiggerr.sims.mapper.account.AccountMapper;
import com.ebiggerr.sims.service.account.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping(path = "/user/authenticate") //If there is any update to this path, have to update in WebSecurity_Config too
    public API_RESPONSE getToken(@RequestBody UserName_Password_Input input) {

        Authentication auth = null;

        try {

            auth = _authenticationManager.authenticate(new UsernamePasswordAuthenticationToken( input.getUsername(), input.getPassword() ));
            SecurityContextHolder.getContext().setAuthentication(auth);

            if( auth.isAuthenticated() ){

                String token = Token_Provider.generateTokenFromAuthentication(auth);
                TokenDto tokenDto = TokenDto.CreateTokenDto(token);

                return new API_RESPONSE().Success(tokenDto);
            }


        }catch (UsernameNotFoundException | CustomException e){
            //Never gonna reach here although the loadUserByUsername() inside the authenticateManager throws UsernameNotFoundException
            //already get handle in the authenticateManager
            if( e instanceof UsernameNotFoundException ){
                return new API_RESPONSE().NotFound("No user with this username found : " + input.getUsername() );
            }
            else{
                logger.error( "Something went wrong when doing authentication. " + e.getMessage() );
                return new API_RESPONSE().Error();
            }
        }
        return new API_RESPONSE().Error();
    }


    @GetMapping(path = "/user/username/" )
    public API_RESPONSE getAUserUsingUsername(@RequestBody GetAccountInput input){

        try {
            Account acc = (Account) _accountService.loadUserByUsername(input.getUsername(), false);
            AccountOutput accDto = AccountMapper.INSTANCE.accountToAccountDto(acc);

            return new API_RESPONSE().Success(accDto);

        }catch (UsernameNotFoundException exception){
            return new API_RESPONSE().NotFound(exception.getMessage());
        }
    }

    @GetMapping(path = "/user/username/{username}" )
    public API_RESPONSE getAUserUsingUsername_Path(@PathVariable String username){

        try {
            Account acc = (Account) _accountService.loadUserByUsername(username, false);
            AccountOutput accDto = AccountMapper.INSTANCE.accountToAccountDto(acc);

            return new API_RESPONSE().Success(accDto);

        }catch (UsernameNotFoundException exception){
            return new API_RESPONSE().NotFound(exception.getMessage());
        }
    }


    @GetMapping(path = "/user/id/{id}" )
    public API_RESPONSE getAUserUsingId(@PathVariable String id){

        try {
            Account acc = (Account) _accountService.loadUserById(id);
            AccountOutput accDto = AccountMapper.INSTANCE.accountToAccountDto(acc);

            return new API_RESPONSE().Success(accDto);

        }catch (UsernameNotFoundException exception){
            return new API_RESPONSE().NotFound(exception.getMessage());
        }
    }

    /**
     * Endpoint for account registration
     *
     * @param input DTO for registration
     * @return API response that tells the user of the result of the registration
     */
    @PostMapping(path = "/user/register") //If there is any update to this path, have to update in WebSecurity_Config too
    public API_RESPONSE registerAnAccount(@RequestBody CreateAccountInput input){

        boolean success = false;

        try{
            success = _accountService.registerAnAccount(input);

            if(!success) return new API_RESPONSE().Error();

        }catch (Exception e){
            return new API_RESPONSE().Error();
        }

        return new API_RESPONSE().Success();
    }

    /**
     * Endpoint for user with administrative privileges to assign roles to an account
     *
     *
     *
     * @param token For the purpose of username extraction, the username in the token
     *              would be used to log in the database that who grant the roles to the
     *              target account
     * @param input DTO, containing the username of the target account and the roles string
     *              ( commas separated, for example : "Inventory Manager,Staff" ) - Containing two
     *              roles in this example
     * @return API response that tells the user of the result of the registration
     */
    @PreAuthorize("hasAnyAuthority('Admin')")
    @PostMapping(path = "/account/roles")
    public API_RESPONSE assigningRolesToAnAccount(@RequestHeader(name="Authorization") String token, @RequestBody UpdateRolesInput input){

        // username of account that made the POST request
        String username = null;
        Result result = null;
        boolean success = false;

        try{
            username = Token_Provider.getUsernameFromToken(token);
        }catch (CustomException e){
            return new API_RESPONSE().Error();
        }

        if(username != null){

            // target account that will have roles assigned to
            try {
                Account acc = (Account) _accountService.loadUserByUsername(input.username, false);

                result = _accountService.assigningRolesToAnAccount(acc, input, username);

            }catch (UsernameNotFoundException e){
                return new API_RESPONSE().NotFound(e.getMessage());
            }

            // Get the roles of input DTO by calling the Role Service

            // Assume that the roles input from DTO will be more than one

            // Get back a list of role

            // Loop the list to create a list of RoleDetails and then save them into database

            if(result.status){
                return new API_RESPONSE().Success("Roles have assigned to account with username of :" + input.username);
            }
            else{
                return new API_RESPONSE().Failed(result.message);
            }
        }

        return new API_RESPONSE().Error();
    }

    @PreAuthorize("hasAnyAuthority('Admin')")
    @DeleteMapping(path = "/account/roles")
    public API_RESPONSE revokeRolesAssignedToAnAccount(@RequestHeader(name="Authorization") String token, @RequestBody UpdateRolesInput input){

        // username of account that made the POST request
        String username = null;
        Result result = null;
        boolean success = false;

        try{
            username = Token_Provider.getUsernameFromToken(token);
        }catch (CustomException e){
            return new API_RESPONSE().Error();
        }

        if(username != null){

            // target account that will have roles assigned to
            try {
                Account acc = (Account) _accountService.loadUserByUsername(input.username, false);

                result = _accountService.revokingRolesToAnAccount(acc, input, username);

            }catch (UsernameNotFoundException e){
                return new API_RESPONSE().NotFound(e.getMessage());
            }

            // Get the roles of input DTO by calling the Role Service

            // Assume that the roles input from DTO will be more than one

            // Get back a list of role

            // Loop the list to create a list of RoleDetails and then save them into database

            if(result.status){
                return new API_RESPONSE().Success("Revoked roles : " + input.roles + " assigned to account with username of :" + input.username);
            }
            else{
                return new API_RESPONSE().Failed(result.message);
            }
        }

        return new API_RESPONSE().Error();
    }
}
