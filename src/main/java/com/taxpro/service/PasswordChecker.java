package com.taxpro.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordChecker {

    private static final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    public boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            // $wp$ prefix remove karo
            String fixedHash = hashedPassword;
            while (fixedHash.contains("$wp$")) {
                fixedHash = fixedHash.replace("$wp$", "");
            }
            // $2y$ ko $2a$ se replace karo
            fixedHash = fixedHash.replace("$2y$", "$2a$");
            
            // Manually $ wapas lagao
            if (!fixedHash.startsWith("$")) {
                fixedHash = "$" + fixedHash;
            }

            System.out.println(">>> Final hash being checked: " + fixedHash);
            return bcrypt.matches(plainPassword, fixedHash);
        } catch (Exception e) {
            System.err.println("Password check error: " + e.getMessage());
            return false;
        }
    }
}