package com.example.springSecurityFormBasedLogin.controller;


import com.example.springSecurityFormBasedLogin.Repository.UserRepository;
import com.example.springSecurityFormBasedLogin.dto.TwoFactorModel;
import com.example.springSecurityFormBasedLogin.dto.UserEntityDto;
import com.example.springSecurityFormBasedLogin.interfaces.TotpManager;
import com.example.springSecurityFormBasedLogin.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final TotpManager totpManager;

    @GetMapping
    public String getUsersPage(Model model){
        model.addAttribute("users", userRepo.findAll().stream().map(UserEntityDto::fromEntity).toList());
        return "users";
    }

    @GetMapping("/profile")
    public String getProfilePage(Principal principal, Model model){
        String email = principal.getName();
        Optional<UserEntity> usr = userRepo.findByEmail(email);
        model.addAttribute("authenticatedUser", UserEntityDto.fromEntity(usr.get()));
        return "profile";
    }

    @GetMapping("/profile/mfa")
    public String updateMfa(@RequestParam("enable") boolean enable2FA,
                            Principal principal,
                            Model model){
        UserEntity usr = userRepo.findByEmail(principal.getName()).get();
        String secretCode = totpManager.generateSecret();

        if(enable2FA){
            TwoFactorModel twoFactorModel = new TwoFactorModel();
            twoFactorModel.setSecret(secretCode);
            twoFactorModel.setActiveUserEmail(usr.getEmail());
            twoFactorModel.setQrCode(totpManager.getUriForImage(secretCode));

            model.addAttribute("twoFactorModel", twoFactorModel);
        }else{
            usr.setTwo_factor_enabled(false);
            usr.setTwo_factor_secret(null);
            userRepo.save(usr);
        }
        model.addAttribute("authenticatedUser", userRepo.findByEmail(principal.getName()).get());

        return "profile";
    }

    @PostMapping("/profile/enable-two-factor")
    public String updateTwoFactor(Model model,
                                  @ModelAttribute TwoFactorModel twoFactorModel,
                                  Principal principal){
        UserEntity usr = userRepo.findByEmail(principal.getName()).get();

        if(twoFactorModel.getActiveUserEmail().equals(principal.getName())){
            if(totpManager.verifyCode(twoFactorModel.getGeneratedCode(), twoFactorModel.getSecret())){
                usr.setTwo_factor_secret(twoFactorModel.getSecret());
                usr.setTwo_factor_enabled(true);
                userRepo.save(usr);
                model.addAttribute("success_code", "changes applied successfully !");
            }else{
                model.addAttribute("wrong_code", "Wrong code, try again !");
                model.addAttribute("twoFactorModel", twoFactorModel);
            };
        }
        model.addAttribute("authenticatedUser", usr);
        return "profile";
    }
}
