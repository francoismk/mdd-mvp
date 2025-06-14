package com.example.mdd_backend.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieOrHeaderBearerTokenResolver implements BearerTokenResolver {
    private final DefaultBearerTokenResolver defaultResolver = new DefaultBearerTokenResolver();

    @Override
    public String resolve(HttpServletRequest request) {
        // First, try to resolve the token from the Authorization header
        String token = defaultResolver.resolve(request);

        // If the token is not found in the header, check for a cookie named "token"
        if (token == null && request.getCookies() != null) {
            System.out.println("Checking cookies for token...");

            token = Arrays.stream(request.getCookies())
                    .filter(cookie -> "token".equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            if (token != null) {
                System.out.println("JWT from cookie: " + token);
            } else {
                System.out.println("No token found in cookies.");
            }
        } else if (token != null) {
            System.out.println("JWT from header: " + token);
        }
        return token;

    }
}
