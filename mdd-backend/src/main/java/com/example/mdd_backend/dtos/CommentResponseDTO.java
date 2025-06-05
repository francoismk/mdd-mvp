package com.example.mdd_backend.dtos;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponseDTO {

    private String id;
    private String content;
    private Date createdAt;
    private GetUserDTO author;
}
