package com.example.springSecurityFormBasedLogin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private CustomDaoAuthenticationProvider authenticationProvider;
    private CustomAuthenticationFailureHandler authenticationFailureHandler;
    private CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomDaoAuthenticationProvider authenticationProvider,
                          CustomUserDetailsService userDetailsService,
                          CustomAuthenticationFailureHandler authenticationFailureHandler
                          ) {
        this.authenticationProvider = authenticationProvider;
        this.userDetailsService = userDetailsService;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        http.csrf(csrf->csrf.disable());
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/login",
                                "/h2-console/**",
                                "/register",
                                "/").permitAll()
                        .anyRequest().authenticated());

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/users")
                .failureHandler(authenticationFailureHandler));

        http.logout(logout->logout
                .logoutUrl("/logout")
                .permitAll());
        return http.build();
    }
}

