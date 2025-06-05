package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.ArticleResponseDTO;
import com.example.mdd_backend.dtos.TopicResponseDTO;
import com.example.mdd_backend.errors.exceptions.DatabaseOperationException;
import com.example.mdd_backend.errors.exceptions.ResourceNotFoundException;
import com.example.mdd_backend.models.DBTopic;
import com.example.mdd_backend.repositories.TopicRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TopicService {

    private static final Logger logger = LoggerFactory.getLogger(
        TopicService.class
    );

    private final TopicRepository topicRepository;
    private final ModelMapper modelMapper;

    public TopicService(
        TopicRepository topicRepository,
        ModelMapper modelMapper
    ) {
        this.topicRepository = topicRepository;
        this.modelMapper = modelMapper;
    }

    public TopicResponseDTO createTopic(ArticleResponseDTO topicDTO) {
        try {
            DBTopic topic = modelMapper.map(topicDTO, DBTopic.class);
            DBTopic savedTopic = topicRepository.save(topic);

            return modelMapper.map(savedTopic, TopicResponseDTO.class);
        } catch (Exception e) {
            logger.error("Error creating topic: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to create topic");
        }
    }

    public TopicResponseDTO getTopicById(String topicId) {
        try {
            DBTopic topic = topicRepository
                .findById(topicId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Theme not found with ID : " + topicId
                    )
                );

            return modelMapper.map(topic, TopicResponseDTO.class);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving topic with ID: {}", topicId, e);
            throw new DatabaseOperationException("Failed to retrieve topic");
        }
    }

    public List<TopicResponseDTO> getAllTopics() {
        try {
            List<DBTopic> topics = topicRepository.findAll();
            List<TopicResponseDTO> topicsDtos = topics
                .stream()
                .map(topic -> modelMapper.map(topic, TopicResponseDTO.class))
                .collect(Collectors.toList());

            return topicsDtos;
        } catch (Exception e) {
            logger.error("Error retrieving all topics: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve topics");
        }
    }

    public void deleteTheme(String topicId) {
        try {
            topicRepository
                .findById(topicId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Theme not found with ID : " + topicId
                    )
                );

            topicRepository.deleteById(topicId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error(
                "Error deleting topic with ID {}: {}",
                topicId,
                e.getMessage(),
                e
            );
            throw new DatabaseOperationException("Failed to delete topic");
        }
    }
}
