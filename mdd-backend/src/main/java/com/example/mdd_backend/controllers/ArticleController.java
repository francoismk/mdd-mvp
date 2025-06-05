package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.ArticleCreateRequestDTO;
import com.example.mdd_backend.dtos.ArticleResponseDTO;
import com.example.mdd_backend.services.ArticleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@Tag(name = "Articles", description = "Article management operations")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    public ResponseEntity<ArticleResponseDTO> createArticle(
        @Valid @RequestBody ArticleCreateRequestDTO articleDTO,
        Authentication authentication
    ) {
        if (articleDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String authorEmail = authentication.getName();

        ArticleResponseDTO createdArticle = articleService.createArticle(
            articleDTO,
            authorEmail
        );
        return new ResponseEntity<>(createdArticle, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponseDTO>> getAllArticles(
        @RequestParam(name = "sort", defaultValue = "date_asc") String sortOrder
    ) {
        List<ArticleResponseDTO> articles = articleService.getArticlesSorted(
            sortOrder
        );

        if (articles == null || articles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> getArticleById(
        @PathVariable String id
    ) {
        ArticleResponseDTO article = articleService.getArticleById(id);

        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArticleById(@PathVariable String id) {
        articleService.deleteArticle(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
