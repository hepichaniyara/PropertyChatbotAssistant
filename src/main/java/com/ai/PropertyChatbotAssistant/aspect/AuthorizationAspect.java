package com.ai.PropertyChatbotAssistant.aspect;

import com.ai.PropertyChatbotAssistant.annotation.RolesAllowed;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorizationAspect {

    @Before("@within(rolesAllowed) || @annotation(rolesAllowed)")
    public void checkRoles(JoinPoint joinPoint, RolesAllowed rolesAllowed) throws Throwable {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !isUserInRole(authentication, rolesAllowed.hasRole())) {
                throw new SecurityException("User does not have the necessary roles to access this resource.");
            }
    }

    private boolean isUserInRole(Authentication authentication, String[] roles) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            for (String role : roles) {
                if (authority.getAuthority().equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }
}
