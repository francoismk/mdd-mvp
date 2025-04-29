package com.example.mdd_backend.models;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "themes")
public class DBTheme {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private String name;

}
