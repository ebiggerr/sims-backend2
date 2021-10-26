package com.ebiggerr.sims.config.jwt;

import com.ebiggerr.sims.service.account.AccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class WebSecurity_Config extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;

    private final UnauthorizedEntryPoint unauthorizedEntryPoint;

    public WebSecurity_Config(AccountService accountService, UnauthorizedEntryPoint unauthorizedEntryPoint) {
        this.unauthorizedEntryPoint = unauthorizedEntryPoint;
        this.accountService = accountService;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.parentAuthenticationManager(null);
        auth.userDetailsService(accountService).passwordEncoder( encoder() );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/authenticate", "/user/register","/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore( authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JWTAuthentication_Filter authenticationTokenFilterBean() throws Exception {
        return new JWTAuthentication_Filter();
    }

    /*@Bean
    public CORS_Filter corsFilter() throws Exception{
        return new CORS_Filter();
    }*/

    @Bean
    public AuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
        provider.setPasswordEncoder(encoder());
        provider.setUserDetailsService(accountService);

        return provider;
    }

}
