package com.ai.PropertyChatbotAssistant.controller;

import com.ai.PropertyChatbotAssistant.model.User;
import com.ai.PropertyChatbotAssistant.service.JwtTokenProvider;
import com.ai.PropertyChatbotAssistant.service.UserDetailsServiceImpl;
import com.ai.PropertyChatbotAssistant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/auth/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginUser) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginUser.getUsername());
        if (Objects.nonNull(userDetails)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(userDetails);
            String token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        //clear context on successfully logout
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("User logged out successfully");
    }
}
