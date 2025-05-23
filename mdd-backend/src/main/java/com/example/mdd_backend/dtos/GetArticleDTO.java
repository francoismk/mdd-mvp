package com.example.mdd_backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class GetArticleDTO {
    private String id;
    private String title;
    private String content;
    private Date date;
    private GetUserDTO author;
    private GetThemeDTO theme;
    private List<GetCommentDTO> comments;
}
