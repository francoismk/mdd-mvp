package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.CreateUserDTO;
import com.example.mdd_backend.dtos.JWTResponseDTO;
import com.example.mdd_backend.dtos.LoginUserDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthService(JWTService jwtService, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    public JWTResponseDTO authenticateAndGenerateToken(LoginUserDTO loginUserDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDTO.getUsernameOrEmail(),
                        loginUserDTO.getPassword()
                ));

                return jwtService.getToken(authentication);
    }

    public JWTResponseDTO registerAndGenerateToken(CreateUserDTO userDTO) {
        userService.createUser(userDTO);
        return jwtService.getTokenFromUserIdentifier(userDTO.getEmail());
    }
}
