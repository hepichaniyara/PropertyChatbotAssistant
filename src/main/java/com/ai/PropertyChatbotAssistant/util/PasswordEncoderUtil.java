package com.ai.PropertyChatbotAssistant.util;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderUtil {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Encode a raw password
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // Check if a raw password matches the encoded password
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
