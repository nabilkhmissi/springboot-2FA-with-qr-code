package com.example.springSecurityFormBasedLogin.controller;

import com.example.springSecurityFormBasedLogin.Repository.UserRepository;
import com.example.springSecurityFormBasedLogin.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String signupInit(Model model){
        model.addAttribute("user", new UserEntity());
        return "signup";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserEntity user,
                               Model model){
        if(user != null && !user.getEmail().isBlank() && !user.getPassword().isBlank() &&!user.getName().isBlank()){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setAuthorities("ROLE_USER");
            user.setProviders("local");
            userRepo.save(user);
            model.addAttribute("signup_success", "your account is created successfully !");
            return "login";
        }

        model.addAttribute("user", user);
        return "redirect:/register";
    }
}


