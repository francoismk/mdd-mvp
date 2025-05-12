package com.example.mdd_backend.repositories;

import com.example.mdd_backend.models.DBTheme;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ThemeRepository extends MongoRepository<DBTheme, String> {}
