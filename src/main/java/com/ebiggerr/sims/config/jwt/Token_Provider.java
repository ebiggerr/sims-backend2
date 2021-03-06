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

    private final static String privateKey = "jXn2r5u8x!A%D*G-KaPdSgVkYp3s6v9y";
    private final static Algorithm algorithm = Algorithm.HMAC256(privateKey);

    private static final String HEADER_STRING="Authorization";
    private static final String TOKEN_PREFIX="Bearer";
    private static final String ISSUER="auth0"; //JWT issuer
    private static final String USERNAME_PRIVATE_CLAIM="username";
    private static final String ROLES_PRIVATE_CLAIM = "roles";

    /**
     *
     * @param authentication Authentication object that contains the Principals, Credentials and Roles/Authorities
     * @return [String] JSON Web Token
     *
     * Claims: Issuer, IssuedAt, ExpiredAt, Username, Roles
     *
     */
    public static String generateTokenFromAuthentication(Authentication authentication) throws CustomException {

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
                        .withClaim(USERNAME_PRIVATE_CLAIM, username)   //account username of the JWT issuing to
                        .withClaim(ROLES_PRIVATE_CLAIM, authorities) //Example- roles: "Staff","Manager"
                        .withIssuedAt(now)
                        .withExpiresAt(exp)
                        .withIssuer(ISSUER)
                        .sign(algorithm);
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
    public static DecodedJWT verifyAndDecodeToken(String token) throws CustomException {

        try{
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
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
    public static UsernamePasswordAuthenticationToken getAuthenticationToken (DecodedJWT decodedJWT, final Authentication existingAuth, String username) throws CustomException {

        if( decodedJWT != null && username != null ) {

            final Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(decodedJWT.getClaim(ROLES_PRIVATE_CLAIM).asString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

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
    public static String getUsernameFromToken(String token) throws CustomException {

        try {
            token = token.replace(TOKEN_PREFIX, "").trim();

            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);

            token = decodedJWT.getClaim(USERNAME_PRIVATE_CLAIM).asString();

            return token; //username from JWT

        }catch (JWTVerificationException exception){
            logger.error("Failed to get the username from token.");
            throw new CustomException("Failed to verify the token and get the username claim from the token");
        }
    }

}
