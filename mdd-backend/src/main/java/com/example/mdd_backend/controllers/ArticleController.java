package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.CreateArticleDTO;
import com.example.mdd_backend.dtos.GetArticleDTO;
import com.example.mdd_backend.services.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    public ResponseEntity<GetArticleDTO> createArticle(@Valid @RequestBody CreateArticleDTO articleDTO, Authentication authentication) {
        if (articleDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String authorEmail = authentication.getName();

        GetArticleDTO createdArticle = articleService.createArticle(articleDTO, authorEmail);
        return new ResponseEntity<>(createdArticle, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GetArticleDTO>> getAllArticles(@RequestParam(name = "sort", defaultValue = "asc") String sortOrder) {
        List<GetArticleDTO> articles = articleService.getArticlesSorted(sortOrder);

        if (articles == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetArticleDTO> getArticleById(@PathVariable String id) {
        GetArticleDTO article = articleService.getArticleById(id);

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
