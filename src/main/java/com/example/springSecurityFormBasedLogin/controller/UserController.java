package com.example.springSecurityFormBasedLogin.controller;


import com.example.springSecurityFormBasedLogin.Repository.UserRepository;
import com.example.springSecurityFormBasedLogin.dto.UserEntityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;

    @GetMapping
    public String getUsersPage(Model model){
        model.addAttribute("users", userRepo.findAll().stream().map(UserEntityDto::fromEntity).toList());
        return "users";
    }
}
