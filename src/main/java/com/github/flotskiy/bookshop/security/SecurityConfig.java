package com.github.flotskiy.bookshop.security;

import com.github.flotskiy.bookshop.security.jwt.InactiveJwtService;
import com.github.flotskiy.bookshop.security.jwt.JWTRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTRequestFilter jwtRequestFilter;
    private final InactiveJwtService inactiveJwtService;

    @Autowired
    public SecurityConfig(
            BookstoreUserDetailsService bookstoreUserDetailsService,
            JWTRequestFilter jwtRequestFilter,
            InactiveJwtService inactiveJwtService
            ) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.inactiveJwtService = inactiveJwtService;
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(bookstoreUserDetailsService)
                .passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/my", "/profile").authenticated()
                .antMatchers("/**").permitAll()
                .and().formLogin()
                .loginPage("/signin").failureUrl("/signin")
                .and().logout().logoutUrl("/logout")
                .addLogoutHandler(((request, response, authentication) -> inactiveJwtService.putJwtToInactiveRepo(request)))
                .logoutSuccessUrl("/")
                .deleteCookies("token").clearAuthentication(true)
                .and().oauth2Login()
                .defaultSuccessUrl("/my")
                .and().oauth2Client();

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
