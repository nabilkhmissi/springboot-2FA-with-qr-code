package com.example.springSecurityFormBasedLogin.interfaces;


public interface TotpManager {
    String generateSecret();
    String getUriForImage(String secret);
    boolean verifyCode(String code, String secret);
}
