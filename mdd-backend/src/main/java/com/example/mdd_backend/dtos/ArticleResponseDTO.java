package com.example.mdd_backend.dtos;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleResponseDTO {

    private String id;
    private String title;
    private String content;
    private Date createdAt;
    private UserResponseDTO author;
    private TopicResponseDTO topic;
    private List<CommentResponseDTO> comments;
}
