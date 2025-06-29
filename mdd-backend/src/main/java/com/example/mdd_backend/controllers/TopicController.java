package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.ArticleResponseDTO;
import com.example.mdd_backend.dtos.TopicCreateRequestDTO;
import com.example.mdd_backend.dtos.TopicResponseDTO;
import com.example.mdd_backend.services.TopicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/topics")
@Tag(name = "Topics", description = "Topic management operations")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    /**
     * Creates a new topic.
     *
     * @param topicDTO The DTO containing the topic's information. Must be a valid object.
     * @return ResponseEntity containing the created TopicResponseDTO if successful,
     *         or an HTTP 400 Bad Request status if the topicDTO is null.
     */
    @PostMapping
    public ResponseEntity<TopicResponseDTO> createTheme(
        @Valid @RequestBody TopicCreateRequestDTO topicDTO
    ) {
        if (topicDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        TopicResponseDTO createdTheme = topicService.createTopic(topicDTO);
        return new ResponseEntity<>(createdTheme, HttpStatus.CREATED);
    }

    /**
     * Retrieves a topic by its ID.
     *
     * @param id The ID of the topic to retrieve.
     * @return ResponseEntity containing the TopicResponseDTO if found,
     *         or an HTTP 404 Not Found status if the topic does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDTO> getThemeById(
        @PathVariable String id
    ) {
        TopicResponseDTO theme = topicService.getTopicById(id);

        if (theme == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(theme, HttpStatus.OK);
    }

    /**
     * Retrieves all topics.
     *
     * @return ResponseEntity containing a list of TopicResponseDTO if topics exist,
     *         or an HTTP 204 No Content status if no topics are found.
     */
    @GetMapping
    public ResponseEntity<List<TopicResponseDTO>> getAllThemes() {
        List<TopicResponseDTO> themes = topicService.getAllTopics();

        if (themes == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(themes, HttpStatus.OK);
    }

    /**
     * Deletes a topic by its ID.
     *
     * @param id The ID of the topic to delete.
     * @return ResponseEntity with an HTTP 200 OK status upon successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteThemeById(@PathVariable String id) {
        topicService.deleteTheme(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
