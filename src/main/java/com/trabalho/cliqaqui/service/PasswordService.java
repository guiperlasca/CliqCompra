package com.trabalho.cliqaqui.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService() {
        // Instantiate BCryptPasswordEncoder directly.
        // If this service were managed by a fuller Spring Security context,
        // PasswordEncoder could be injected, but for a standalone service this is fine.
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Hashes a plain text password using BCrypt.
     * @param plainPassword The plain text password.
     * @return The BCrypt hashed password.
     */
    public String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            return null; // Or throw an IllegalArgumentException
        }
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Checks if a plain text password matches a stored BCrypt hashed password.
     * @param plainPassword The plain text password to check.
     * @param hashedPassword The stored hashed password.
     * @return true if the passwords match, false otherwise.
     */
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false; // Or handle appropriately
        }
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
