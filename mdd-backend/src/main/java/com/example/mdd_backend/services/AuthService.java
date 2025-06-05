package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.AuthResponseDTO;
import com.example.mdd_backend.dtos.LoginRequestDTO;
import com.example.mdd_backend.dtos.UserCreateRequestDTO;
import com.example.mdd_backend.errors.exceptions.AuthenticationException;
import com.example.mdd_backend.errors.exceptions.DuplicateResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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

    public AuthResponseDTO authenticateAndGenerateToken(
        LoginRequestDTO LoginRequestDTO
    ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    LoginRequestDTO.getUsernameOrEmail(),
                    LoginRequestDTO.getPassword()
                )
            );

            return jwtService.getToken(authentication);
        } catch (Exception e) {
            logger.warn(
                "Authentication failed for user: {}",
                LoginRequestDTO.getUsernameOrEmail()
            );
            throw new AuthenticationException("Invalid credentials");
        }
    }

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
}
