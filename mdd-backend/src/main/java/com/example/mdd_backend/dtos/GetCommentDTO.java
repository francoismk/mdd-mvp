package com.example.mdd_backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GetCommentDTO {

    private String id;
    private String content;
    private Date createdAt;
    private GetUserDTO author;
}
