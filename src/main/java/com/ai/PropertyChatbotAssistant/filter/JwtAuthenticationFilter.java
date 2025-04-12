package com.ai.PropertyChatbotAssistant.filter;


import com.ai.PropertyChatbotAssistant.service.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtTokenProvider.extractToken(request);

        if (token != null) {
            log.info("Token is : {}", token);
            if (jwtTokenProvider.validateToken(token)){
                SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthentication(token));
            }
        }

        filterChain.doFilter(request, response);
    }
}

