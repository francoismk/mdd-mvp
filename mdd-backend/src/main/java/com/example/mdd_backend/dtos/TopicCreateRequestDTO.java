package com.example.mdd_backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicCreateRequestDTO {

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Description is required")
    private String description;
}
