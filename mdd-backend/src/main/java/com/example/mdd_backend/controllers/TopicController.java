package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.ArticleResponseDTO;
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

    @PostMapping
    public ResponseEntity<TopicResponseDTO> createTheme(
        @Valid @RequestBody ArticleResponseDTO topicDTO
    ) {
        if (topicDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        TopicResponseDTO createdTheme = topicService.createTopic(topicDTO);
        return new ResponseEntity<>(createdTheme, HttpStatus.CREATED);
    }

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

    @GetMapping
    public ResponseEntity<List<TopicResponseDTO>> getAllThemes() {
        List<TopicResponseDTO> themes = topicService.getAllTopics();

        if (themes == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(themes, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteThemeById(@PathVariable String id) {
        topicService.deleteTheme(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
