package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.AuthResponseDTO;
import com.example.mdd_backend.dtos.LoginRequestDTO;
import com.example.mdd_backend.dtos.UserCreateRequestDTO;
import com.example.mdd_backend.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(
    name = "Authentication",
    description = "Authentication management operations"
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> createUser(
        @Valid @RequestBody UserCreateRequestDTO userDTO
    ) {
        AuthResponseDTO token = authService.registerAndGenerateToken(userDTO);
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(
        @Valid @RequestBody LoginRequestDTO LoginRequestDTO
    ) {
        AuthResponseDTO token = authService.authenticateAndGenerateToken(
            LoginRequestDTO
        );

        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
