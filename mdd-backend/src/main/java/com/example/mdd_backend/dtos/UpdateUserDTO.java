package com.example.mdd_backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDTO {
    private String email;
    private String username;
    private String password;
    private String currentPassword;
}
