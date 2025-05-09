package com.example.mdd_backend.repositories;

import com.example.mdd_backend.models.DBArticle;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleRepository extends MongoRepository<DBArticle, UUID> {}
