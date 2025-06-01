package com.example.mdd_backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTopicDTO {

    @NotNull(message = "Name is required")
    private String name;
}
