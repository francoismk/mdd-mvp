package com.example.mdd_backend.models;

import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "articles")
public class DBArticle {

    @Id
    private String id;

    private String title;
    private String content;
    private Date createdAt;
    private String authorId;
    private String topicId;
}
