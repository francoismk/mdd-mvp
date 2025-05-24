package com.example.mdd_backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWTResponseDTO {
    private String token;

    public JWTResponseDTO(String token) {
        this.token = token;
    }
}
