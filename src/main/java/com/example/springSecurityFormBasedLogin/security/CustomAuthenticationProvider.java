package com.example.springSecurityFormBasedLogin.security;

import com.example.springSecurityFormBasedLogin.interfaces.TotpManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
    private TotpManager totpManager;

    public void setMfaTokenManager(TotpManager totpManager) {
        this.totpManager = totpManager;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String password = (String) authentication.getCredentials();
        String email = authentication.getName();

        CustomUserDetails principal = (CustomUserDetails) getUserDetailsService().loadUserByUsername(email);

        return setAuthentication(principal, password, principal.isTwoFactorEnabled());
    }

    private UsernamePasswordAuthenticationToken setAuthentication(CustomUserDetails principal,
                                                                  String password,
                                                                  boolean twoStepEnabled) {
        String TOTP_AUTHORITY = "TOTP_AUTH_AUTHORITY";
        UsernamePasswordAuthenticationToken authentication = null;
        Collection<? extends GrantedAuthority> authorities = null;

        if (!getPasswordEncoder().matches(password, principal.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        //if user enable 2FA, give him a totp_authority and erase other roles
        authorities = !twoStepEnabled ? principal.getAuthorities() : List.of(new SimpleGrantedAuthority(TOTP_AUTHORITY));


        authentication = new UsernamePasswordAuthenticationToken(
                principal, password, authorities);

        authentication.setDetails(authentication.getDetails());
        return authentication;
    }

}