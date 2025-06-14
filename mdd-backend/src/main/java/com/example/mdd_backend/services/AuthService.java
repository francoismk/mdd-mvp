package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.AuthResponseDTO;
import com.example.mdd_backend.dtos.LoginRequestDTO;
import com.example.mdd_backend.dtos.UserCreateRequestDTO;
import com.example.mdd_backend.errors.exceptions.AuthenticationException;
import com.example.mdd_backend.errors.exceptions.DuplicateResourceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Service for user authentication and registration.
 *
 * Handles login, registration and JWT token generation.
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(
        AuthService.class
    );

    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthService(
        JWTService jwtService,
        AuthenticationManager authenticationManager,
        UserService userService
    ) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    /**
     * Authenticates user and generates JWT token.
     *
     * Supports login with either username or email.
     *
     * @param loginRequestDTO User credentials (username/email + password)
     * @return JWT token for authenticated user
     * @throws AuthenticationException If credentials are invalid
     */
    public AuthResponseDTO authenticateAndGenerateToken(
        LoginRequestDTO loginRequestDTO
    ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequestDTO.getUsernameOrEmail(),
                    loginRequestDTO.getPassword()
                )
            );

            return jwtService.getToken(authentication);
        } catch (Exception e) {
            logger.warn(
                "Authentication failed for user: {}",
                loginRequestDTO.getUsernameOrEmail()
            );
            throw new AuthenticationException("Invalid credentials");
        }
    }

    /**
     * Registers new user and generates JWT token.
     *
     * Creates user account and automatically logs them in.
     *
     * @param userDTO New user registration data
     * @return JWT token for newly registered user
     * @throws DuplicateResourceException If email already exists
     * @throws AuthenticationException On registration failure
     */
    public AuthResponseDTO registerAndGenerateToken(
        UserCreateRequestDTO userDTO
    ) {
        try {
            userService.createUser(userDTO);
            return jwtService.getTokenFromUserIdentifier(userDTO.getEmail());
        } catch (DuplicateKeyException e) {
            logger.warn(
                "Registration failed: User with email {} already exists",
                userDTO.getEmail()
            );
            throw new DuplicateResourceException("Registration failed: ");
        } catch (Exception e) {
            logger.error(
                "Registration failed for user: {}",
                userDTO.getEmail(),
                e
            );
            throw new AuthenticationException("Registration failed: ");
        }
    }

    /**
     * Add auth cookie.
     *
     * @param token    the token
     * @param response the response
     */
    public void addAuthCookie(
        AuthResponseDTO token,
        HttpServletResponse response
    ) {
        Cookie cookie = new Cookie("token", token.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24 hours
        response.addCookie(cookie);
    }
}
