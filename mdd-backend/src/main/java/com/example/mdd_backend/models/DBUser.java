package com.example.mdd_backend.models;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class DBUser {
    @Id
    private UUID id;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String username;
    
    private String password;
    private List<String> subscriptions;

}
