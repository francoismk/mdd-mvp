package com.example.mdd_backend.repositories;

import com.example.mdd_backend.models.DBComment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<DBComment, String> {}
