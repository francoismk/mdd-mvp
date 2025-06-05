package com.example.mdd_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "Email or usernale is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;
}
