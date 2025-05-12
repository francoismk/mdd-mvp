package com.example.mdd_backend.models;

import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "comments")
public class DBComment {

    @Id
    private String id;

    private String content;
    private String userId;
    private String articleId;
    private Date date;
}
