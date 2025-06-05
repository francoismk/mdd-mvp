package com.example.mdd_backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateRequestDTO {

    @NotNull(message = "Content is required")
    private String content;
}
