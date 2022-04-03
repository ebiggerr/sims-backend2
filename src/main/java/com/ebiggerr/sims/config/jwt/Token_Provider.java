package com.ebiggerr.sims.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ebiggerr.sims.domain.account.Account;
import com.ebiggerr.sims.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class Token_Provider extends JWT {

    private static final Logger logger = LoggerFactory.getLogger(Token_Provider.class);

    private String TOKEN_PRIVATE_KEY;

    private String TOKEN_PREFIX;

    private String TOKEN_HEADER;

    private String ISSUER;

    private String USERNAME_PRIVATE_CLAIM;

    private String ROLES_PRIVATE_CLAIM;

    public Token_Provider(){

    }

    //The JWTAuthentication_Filter class's way of using @Value works fine there, but not here - null value
    //Thus, took a total different approach to get value from application.properties
    @Autowired
    public Token_Provider(@Value("${jwt.token.secret}") String secret,
                          @Value("${jwt.token.prefix}") String prefix,
                          @Value("${jwt.token.header}") String header,
                          @Value("${jwt.token.issuer}") String issuer,
                          @Value("${jwt.claim.username}") String claim_username,
                          @Value("${jwt.claim.roles}") String claim_roles
                          ){
        this.TOKEN_PRIVATE_KEY = secret;
        this.TOKEN_PREFIX = prefix;
        this.TOKEN_HEADER = header;
        this.ISSUER = issuer;
        this.USERNAME_PRIVATE_CLAIM = claim_username;
        this.ROLES_PRIVATE_CLAIM = claim_roles;
    }

    /**
     *
     * @param authentication Authentication object that contains the Principals, Credentials and Roles/Authorities
     * @return [String] JSON Web Token
     *
     * Claims: Issuer, IssuedAt, ExpiredAt, Username, Roles
     *
     */
    public String generateTokenFromAuthentication(Authentication authentication) throws CustomException {

        final String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        Instant nowEpoch = Instant.now();
        Date now = Date.from(nowEpoch);
        // 30 minutes Expiration Duration
        Date exp = Date.from(nowEpoch.plus(30, ChronoUnit.MINUTES));

        // extract username from Authentication object
        Object principal = authentication.getPrincipal();
        String username = null;
        if (principal instanceof Account) {
             username = ((Account)principal).getUsername();
        } else {
            username = principal.toString();
        }

        try{
            if( username != null ) {

                return JWT.create()
                        .withClaim(getUSERNAME_PRIVATE_CLAIM(), username)   //account username of the JWT issuing to
                        .withClaim(getROLES_PRIVATE_CLAIM(), authorities) //Example- roles: "Staff","Manager"
                        .withIssuedAt(now)
                        .withExpiresAt(exp)
                        .withIssuer(getISSUER())
                        .sign(getAlgorithm());
            }
            else{
                throw new JWTCreationException("Null Username", new Throwable("Null Username") );
            }

        }catch (JWTCreationException exception){
            logger.error( "Something Wrong During the Creation of JWT: " + exception.getMessage() );
            throw new CustomException("Something Wrong During the Creation of JWT");
        }

    }

    /**
     *
     * @param token JSON Web Token
     * @return DecodedJWT from the JSON Web Token
     */
    public DecodedJWT verifyAndDecodeToken(String token) throws CustomException {

        try{
            JWTVerifier verifier = JWT.require(getAlgorithm()).withIssuer(getISSUER()).build();
            return verifier.verify(token);

        }catch (JWTVerificationException exception){
            logger.error("Failed to verify the JWT token.");
            throw new CustomException("Failed to verify the JWT token.");
        }
    }

    /**
     *
     * @param decodedJWT decodedJWT from the Request
     * @param existingAuth always be null
     * @param username username extract from the JWT
     * @return UsernamePasswordAuthenticationToken to be used by SecurityContextHolder.getContext.setAuthentication(), @PreAuthorize on controllers
     */
    public UsernamePasswordAuthenticationToken getAuthenticationToken (DecodedJWT decodedJWT, final Authentication existingAuth, String username) throws CustomException {

        if( decodedJWT != null && username != null ) {

            final Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(decodedJWT.getClaim(getROLES_PRIVATE_CLAIM()).asString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            return new UsernamePasswordAuthenticationToken(username, "", authorities);
        }
        else{
            throw new CustomException("Error occurred during parsing GrantedAuthority collection into UsernamePasswordAuthenticationToken.");
        }
    }

    /**
     *
     * @param token JSON Web Token
     * @return Extracted username from the JWT
     */
    public String getUsernameFromToken(String token) throws CustomException {

        try {
            token = token.replace(getTOKEN_PREFIX(), "").trim();

            DecodedJWT decodedJWT = JWT.require(getAlgorithm()).build().verify(token);

            token = decodedJWT.getClaim(getUSERNAME_PRIVATE_CLAIM()).asString();

            return token; //username from JWT

        }catch (JWTVerificationException exception){
            logger.error("Failed to get the username from token.");
            throw new CustomException("Failed to verify the token and get the username claim from the token");
        }
    }

    public String getPrivateKey(){
        return TOKEN_PRIVATE_KEY;
    }

    public String getTOKEN_HEADER(){
        return TOKEN_HEADER;
    }

    public String getTOKEN_PREFIX(){
        return TOKEN_PREFIX;
    }

    public String getISSUER(){
        return ISSUER;
    }

    public String getUSERNAME_PRIVATE_CLAIM(){
        return USERNAME_PRIVATE_CLAIM;
    }

    public String getROLES_PRIVATE_CLAIM(){
        return ROLES_PRIVATE_CLAIM;
    }

    public Algorithm getAlgorithm(){
        return Algorithm.HMAC256(getPrivateKey());
    }

}
