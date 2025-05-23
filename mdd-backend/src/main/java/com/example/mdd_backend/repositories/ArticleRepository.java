package com.example.mdd_backend.repositories;

import com.example.mdd_backend.models.DBArticle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ArticleRepository extends MongoRepository<DBArticle, String> {
    List<DBArticle> findByThemeId(String themeId);
}
