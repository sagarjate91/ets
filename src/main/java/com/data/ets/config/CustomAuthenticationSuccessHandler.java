package com.data.ets.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // Check user's role and redirect accordingly
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                setDefaultTargetUrl("/admin/dashboard");
                break;
            } else if (authority.getAuthority().equals("ROLE_EMPLOYEE")) {
                setDefaultTargetUrl("/employee/dashboard");
                break;
            }
        }
        
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

