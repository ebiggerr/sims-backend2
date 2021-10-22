package com.ebiggerr.sims.DTO.token;

import java.io.Serializable;

public class TokenDto implements Serializable {

    private String token;

    private TokenDto(){

    }

    private TokenDto(String token){
        this.token = token;
    }

    public static TokenDto CreateTokenDto(String token){
        return new TokenDto(token);
    }

    public String getTokenString() {
        return token;
    }

    public void setTokenString(String token) {
        this.token = token;
    }
}
