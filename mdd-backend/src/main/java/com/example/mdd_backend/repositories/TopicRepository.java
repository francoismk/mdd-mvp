package com.example.mdd_backend.repositories;

import com.example.mdd_backend.models.DBTopic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TopicRepository extends MongoRepository<DBTopic, String> {}
