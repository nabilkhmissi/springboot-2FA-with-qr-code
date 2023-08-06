package com.example.springSecurityFormBasedLogin.security;

import com.example.springSecurityFormBasedLogin.filter.TwoStepVerificationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFailureHandler authenticationFailureHandler;
    private final CustomAuthenticationProvider authenticationProvider;
    private final CustomWebAuthenticationDetailsSource authenticationDetailsSource;
    private final TwoStepVerificationFilter twoStepVerificationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        http.csrf(csrf->csrf.disable());
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/two-step-verification").hasAuthority("TOTP_AUTH_AUTHORITY")
                        .requestMatchers("/login",
                        "/h2-console/**",
                        "/register",
                        "/").permitAll()
                        .anyRequest().hasAnyRole("USER", "ADMIN"));

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
                .authenticationDetailsSource(authenticationDetailsSource)
                .failureHandler(authenticationFailureHandler));

        http.logout(logout->logout
                .logoutUrl("/logout")
                .permitAll());
        http.addFilterBefore(twoStepVerificationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

