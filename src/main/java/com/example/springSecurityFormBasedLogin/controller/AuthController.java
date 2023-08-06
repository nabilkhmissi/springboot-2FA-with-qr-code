package com.example.springSecurityFormBasedLogin.controller;

import com.example.springSecurityFormBasedLogin.Repository.UserRepository;
import com.example.springSecurityFormBasedLogin.interfaces.TotpManager;
import com.example.springSecurityFormBasedLogin.models.UserEntity;
import com.example.springSecurityFormBasedLogin.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;
    private final TotpManager totpManager;

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

    @GetMapping("/two-step-verification")
    public String getTwoStepVerificationPage(){
        return "verification";
    }

    @PostMapping("/wo-step-verification")
    public String checkCode(@RequestParam("verification_code") String code,
                            Principal principal,
                            Model model){
        System.out.println("verification code ==== " + code);
        System.out.println("authentication authority ==== ");
        CustomUserDetails principal1 = (CustomUserDetails) principal;
        principal1.getAuthorities().forEach(System.out::println);
        UserEntity usr = userRepo.findByEmail(principal.getName()).get();
        if(!totpManager.verifyCode(code, usr.getTwo_factor_secret() )){
            model.addAttribute("invalid_code", "invalid code , try again !");
            return "verification";
        }
        //create a new authentication object with user real authorities

        List<SimpleGrantedAuthority> list_authorities = usr.extractAuthorities().stream().map(SimpleGrantedAuthority::new).toList();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(usr, usr.getPassword(), list_authorities);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        return "index";
    }
}


