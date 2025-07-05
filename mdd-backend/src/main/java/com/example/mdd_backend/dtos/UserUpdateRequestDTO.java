package com.example.mdd_backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDTO {

    private String email;
    private String username;
    private String password;
}
