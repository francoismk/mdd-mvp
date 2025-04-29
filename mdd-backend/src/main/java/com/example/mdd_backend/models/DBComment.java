package com.example.mdd_backend.models;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "comments")
public class DBComment {

    @Id
    private UUID id;

    private String content;
    private String userId;
    private String articleId;
    private Date date;

}
