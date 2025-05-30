package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.CreateUserDTO;
import com.example.mdd_backend.dtos.JWTResponseDTO;
import com.example.mdd_backend.dtos.LoginUserDTO;
import com.example.mdd_backend.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<JWTResponseDTO>createUser(@Valid @RequestBody CreateUserDTO userDTO) {
        JWTResponseDTO token = authService.registerAndGenerateToken(userDTO);
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JWTResponseDTO> loginUser(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        JWTResponseDTO token = authService.authenticateAndGenerateToken(loginUserDTO);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
