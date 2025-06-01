package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.CreateTopicDTO;
import com.example.mdd_backend.dtos.GetTopicDTO;
import com.example.mdd_backend.models.DBTopic;
import com.example.mdd_backend.repositories.TopicRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final ModelMapper modelMapper;

    public TopicService(
        TopicRepository topicRepository,
        ModelMapper modelMapper
    ) {
        this.topicRepository = topicRepository;
        this.modelMapper = modelMapper;
    }

    public GetTopicDTO createTopic(CreateTopicDTO topicDTO) {
        DBTopic topic = modelMapper.map(topicDTO, DBTopic.class);
        DBTopic savedTopic = topicRepository.save(topic);

        return modelMapper.map(savedTopic, GetTopicDTO.class);
    }

    public GetTopicDTO getTopicById(String topicId) {
        DBTopic topic = topicRepository
            .findById(topicId)
            .orElseThrow(() ->
                new NoSuchElementException(
                    "Theme not found with ID : " + topicId
                )
            );

        return modelMapper.map(topic, GetTopicDTO.class);
    }

    public List<GetTopicDTO> getAllTopics() {
        List<DBTopic> topics = topicRepository.findAll();
        List<GetTopicDTO> topicsDtos = topics
            .stream()
            .map(topic -> modelMapper.map(topic, GetTopicDTO.class))
            .collect(Collectors.toList());

        return topicsDtos;
    }

    public void deleteTheme(String topicId) {
        topicRepository
            .findById(topicId)
            .orElseThrow(() ->
                new NoSuchElementException(
                    "Theme not found with ID : " + topicId
                )
            );

        topicRepository.deleteById(topicId);
    }
}
