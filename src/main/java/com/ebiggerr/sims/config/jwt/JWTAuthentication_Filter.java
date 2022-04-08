package com.ebiggerr.sims.config.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ebiggerr.sims.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTAuthentication_Filter extends OncePerRequestFilter {

    @Value("${jwt.token.header}")
    private String HEADER_STRING;

    @Value("${jwt.token.prefix}")
    private String TOKEN_PREFIX;

    @Value("${jwt.claim.username}")
    private String USERNAME_PRIVATE_CLAIM;

    private final Token_Provider tokenProvider = new Token_Provider();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String username = null;
        String authToken = null;
        DecodedJWT decodedJWT = null;

        //extract request header
        String header = request.getHeader(HEADER_STRING);

        if ( header != null && header.startsWith(TOKEN_PREFIX) ){
            authToken = header.replace(TOKEN_PREFIX,"").trim(); //extract the token String by eliminate the "Bearer"

            try{
                decodedJWT = tokenProvider.verifyAndDecodeToken(authToken);
                username = decodedJWT.getClaim(USERNAME_PRIVATE_CLAIM).asString();

            }catch ( CustomException | JWTVerificationException exception ){
                logger.debug( "Invalid JWT: " + exception.getMessage() );
            }
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                //extract username and authorities from the decoded JWT
                UsernamePasswordAuthenticationToken authenticationToken =
                        tokenProvider.getAuthenticationToken(decodedJWT, null, username);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //update
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }catch (CustomException exception){
                logger.error(exception.getMessage());
            }

        }

        filterChain.doFilter(request,response);

    }
}
