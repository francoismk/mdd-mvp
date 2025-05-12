package com.example.mdd_backend.repositories;

import com.example.mdd_backend.models.DBUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<DBUser, String> {}
