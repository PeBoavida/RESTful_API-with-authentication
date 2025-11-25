package com.sky.pedroboavida.test.config;

import com.sky.pedroboavida.test.entity.User;
import com.sky.pedroboavida.test.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String swaggerEmail = "swagger-ui@example.com";
        String swaggerPassword = System.getenv("SWAGGER_UI_PASSWORD");
        
        if (swaggerPassword == null || swaggerPassword.isEmpty()) {
            swaggerPassword = "swagger123";
            log.info("SWAGGER_UI_PASSWORD not set, using default password");
        }

        // Check if user exists and if password is valid BCrypt hash
        boolean userExists = userRepository.existsByEmail(swaggerEmail);
        boolean needsRecreation = false;
        
        if (userExists) {
            User existingUser = userRepository.findByEmail(swaggerEmail)
                    .orElseThrow(() -> new RuntimeException("User should exist but was not found"));
            
            String storedPassword = existingUser.getPassword();
            // Check if stored password is a valid BCrypt hash (starts with $2a$, $2b$, or $2y$)
            boolean isValidBCrypt = storedPassword != null && 
                    storedPassword.length() == 60 && 
                    (storedPassword.startsWith("$2a$") || 
                     storedPassword.startsWith("$2b$") || 
                     storedPassword.startsWith("$2y$"));
            
            if (!isValidBCrypt) {
                log.warn("Existing Swagger UI user has invalid password hash, will recreate user");
                needsRecreation = true;
            } else {
                // Password is valid BCrypt, but update it to match current environment variable
                log.info("Swagger UI user already exists with valid BCrypt hash, updating password");
                String newHash = passwordEncoder.encode(swaggerPassword);
                existingUser.setPassword(newHash);
                userRepository.save(existingUser);
                log.info("Updated Swagger UI user password for: {}", swaggerEmail);
            }
        }
        
        // Create new user if it doesn't exist or needs recreation
        if (!userExists || needsRecreation) {
            if (needsRecreation) {
                log.info("Deleting existing Swagger UI user to recreate with valid password");
                userRepository.findByEmail(swaggerEmail).ifPresent(userRepository::delete);
            }
            
            User swaggerUser = new User();
            swaggerUser.setEmail(swaggerEmail);
            String encodedPassword = passwordEncoder.encode(swaggerPassword);
            swaggerUser.setPassword(encodedPassword);
            swaggerUser.setName("Swagger UI Test User");

            userRepository.save(swaggerUser);
            log.info("Created Swagger UI user: {} / {}", swaggerEmail, swaggerPassword);
            log.info("Password hash (first 20 chars): {}", encodedPassword.substring(0, Math.min(20, encodedPassword.length())));
        }
        
        // Verify the user was created/updated correctly
        User verifyUser = userRepository.findByEmail(swaggerEmail)
                .orElseThrow(() -> new RuntimeException("Failed to create/update Swagger UI user"));
        boolean passwordMatches = passwordEncoder.matches(swaggerPassword, verifyUser.getPassword());
        log.info("Swagger UI user verification - Password matches: {}", passwordMatches);
        if (!passwordMatches) {
            log.error("CRITICAL: Swagger UI user password verification failed!");
            throw new RuntimeException("Failed to verify Swagger UI user password");
        }
    }
}

