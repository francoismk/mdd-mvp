package com.example.mdd_backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "themes")
public class DBTheme {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
}
