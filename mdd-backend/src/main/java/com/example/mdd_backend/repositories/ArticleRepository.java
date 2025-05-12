package com.example.mdd_backend.repositories;

import com.example.mdd_backend.models.DBArticle;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleRepository extends MongoRepository<DBArticle, String> {}
