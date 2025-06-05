package com.example.mdd_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "Email or usernale is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;
}
