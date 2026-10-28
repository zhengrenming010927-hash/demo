package com.example;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "Test123!";
        String hash = encoder.encode(password);

        System.out.println("自检：" + encoder.matches(password, hash));

        System.out.println(
            "UPDATE AUTH_ACCOUNTS SET PASSWORD_HASH = '" + hash
            + "' WHERE USERNAME = 'testuser';"
        );
    }
} 