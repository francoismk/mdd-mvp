package com.example.mdd_backend.repositories;

import com.example.mdd_backend.models.DBComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<DBComment, String> {
    List<DBComment> findByArticleId(String articleId);
    void deleteByArticleId(String articleId);
}
