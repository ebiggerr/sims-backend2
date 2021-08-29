/*
 * Copyright (c) 2021 EbiggerR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ebiggerr.sims.config.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ebiggerr.sims.exception.CustomException;
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

    private final String HEADER_STRING="Authorization";
    private final String TOKEN_PREFIX="Bearer";
    private final String USERNAME_PRIVATE_CLAIM="username";

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
                decodedJWT = Token_Provider.verifyAndDecodeToken(authToken);
                username = decodedJWT.getClaim(USERNAME_PRIVATE_CLAIM).asString();

            }catch ( CustomException | JWTVerificationException exception ){
                logger.debug( "Invalid JWT: " + exception.getMessage() );
            }
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                //extract username and authorities from the decoded JWT
                UsernamePasswordAuthenticationToken authenticationToken =
                        Token_Provider.getAuthenticationToken(decodedJWT, null, username);
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
