package com.example.springSecurityFormBasedLogin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TwoFactorModel {
    private String secret;
    private String generatedCode;
    private String activeUserEmail;
    private String qrCode;
}
