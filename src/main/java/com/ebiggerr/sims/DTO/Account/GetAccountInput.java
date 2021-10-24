package com.ebiggerr.sims.DTO.Account;

import javax.validation.constraints.NotBlank;

public class GetAccountInput {

    @NotBlank
    public String username;

    public String getUsername() {
        return username;
    }
}
