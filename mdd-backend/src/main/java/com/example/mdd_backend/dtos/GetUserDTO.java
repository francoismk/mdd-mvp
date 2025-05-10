package com.example.mdd_backend.dtos;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserDTO {

    private UUID id;
    private String email;
    private String username;
}
