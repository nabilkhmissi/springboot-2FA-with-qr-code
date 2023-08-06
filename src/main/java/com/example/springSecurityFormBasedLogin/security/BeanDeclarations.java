package com.example.springSecurityFormBasedLogin.security;

import com.example.springSecurityFormBasedLogin.filter.TwoStepVerificationFilter;
import com.example.springSecurityFormBasedLogin.interfaces.TotpManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class BeanDeclarations {

    private final CustomUserDetailsService userDetailsService;
    private final TotpManager totpManager;

    @Bean
    public CustomAuthenticationProvider authenticationProvider() {
        CustomAuthenticationProvider authenticationProvider
                = new CustomAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setMfaTokenManager(totpManager);
        return authenticationProvider;
    }

    @Bean
    public CustomAuthenticationFailureHandler authenticationFailureHandler(){
        return new CustomAuthenticationFailureHandler();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
