package com.example.springSecurityFormBasedLogin.security;

import com.example.springSecurityFormBasedLogin.Repository.UserRepository;
import com.example.springSecurityFormBasedLogin.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByEmail(username);
        if(!user.isPresent()){
            throw new UsernameNotFoundException("user with this email not found");
        }
        return new CustomUserDetails(user.get());
    }
}
