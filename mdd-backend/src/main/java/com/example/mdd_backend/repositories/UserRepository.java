package com.example.mdd_backend.repositories;

import com.example.mdd_backend.models.DBUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<DBUser, String> {
    Optional<DBUser> findByEmail(String email);
    Optional<DBUser> findByUsername(String username);
}
