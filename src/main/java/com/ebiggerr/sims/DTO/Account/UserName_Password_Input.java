package com.ebiggerr.sims.DTO.Account;

import javax.validation.constraints.NotEmpty;

public class UserName_Password_Input {

    @NotEmpty
    public String username;

    @NotEmpty
    public String password;

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

}
