package com.example.springSecurityFormBasedLogin.filter;

import com.example.springSecurityFormBasedLogin.interfaces.TotpManager;
import com.example.springSecurityFormBasedLogin.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TwoStepVerificationFilter extends OncePerRequestFilter {


    private final TotpManager totpManager;
    private final UserDetailsService userDetailsService;

    private static final String TOTP_AUTHORITY = "TOTP_AUTH_AUTHORITY";
    private static final String REQUEST_TOKEN = "verification_code";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        //is invoked for every request- --> check if there is a TOTP_Authority to block user from accessing the application

        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            chain.doFilter(request, response);
            return;
        }
        String code = request.getParameter(REQUEST_TOKEN);

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            boolean containsTotopAuthority = AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains(TOTP_AUTHORITY);

            if (containsTotopAuthority && !isRequestToVerifyPage(request) && code == null) {
                redirectStrategy.sendRedirect(request, response, "/two-step-verification");
            }
            if (code != null) {
                if (totpManager.verifyCode(code, principal.getTwoFactorSecret())) {
                    CustomUserDetails authenticatedUser = (CustomUserDetails) userDetailsService.loadUserByUsername(principal.getUsername());

                    UsernamePasswordAuthenticationToken newAuthentication
                            = new UsernamePasswordAuthenticationToken(principal, authentication.getCredentials(), authenticatedUser.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(newAuthentication);
                    redirectStrategy.sendRedirect(request, response, "/");
                    return;
                } else {
                    redirectStrategy.sendRedirect(request, response, "/two-step-verification");
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isRequestToVerifyPage(HttpServletRequest request) {
        return request.getRequestURI().endsWith("/two-step-verification");
    }
}
