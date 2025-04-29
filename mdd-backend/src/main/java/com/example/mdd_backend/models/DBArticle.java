package com.example.mdd_backend.models;

import java.util.List;
import java.util.UUID;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "articles")
public class DBArticle {

    @Id
    private UUID id;

    private String title;
    private String content;
    private Date date;
    private String authorId;
    private String themeId;
    private List<String> comments;
}
