package com.example.springSecurityFormBasedLogin.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomDaoAuthenticationProvider implements AuthenticationProvider {

    private PasswordEncoder passwordEncoder;
    private CustomUserDetailsService customUserDetailsService;

    public CustomDaoAuthenticationProvider(PasswordEncoder passwordEncoder,
                                           CustomUserDetailsService customUserDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

            String principal = (String) authentication.getPrincipal();
            CustomUserDetails user = (CustomUserDetails) customUserDetailsService.loadUserByUsername(principal);
            if(user == null){
                throw new UsernameNotFoundException("user with this email not found !");
            }
            String credentials = (String) authentication.getCredentials();
            if(user.getPassword() != null && !passwordEncoder.matches(credentials, user.getPassword())){
                throw new BadCredentialsException("password doesnt match ...");
            }
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(authentication.getDetails());

        return usernamePasswordAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
