package com.sky.pedroboavida.test.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswordHashTest {
    
    @Test
    void generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("swagger123");
        System.out.println("\n========================================");
        System.out.println("BCrypt hash for 'swagger123':");
        System.out.println(hash);
        System.out.println("========================================\n");
        
        // Verify it works
        assert encoder.matches("swagger123", hash);
    }
}

