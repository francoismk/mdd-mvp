package com.example.mdd_backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserDTO {

    private String id;
    private String email;
    private String username;
}
