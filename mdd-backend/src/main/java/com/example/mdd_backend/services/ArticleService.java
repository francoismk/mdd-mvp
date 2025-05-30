package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.*;
import com.example.mdd_backend.models.DBArticle;
import com.example.mdd_backend.repositories.ArticleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ThemeService themeService;
    private final CommentService commentService;

    public ArticleService(ArticleRepository articleRepository, ModelMapper modelMapper, UserService userService, ThemeService themeService, CommentService commentService) {
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.themeService = themeService;
        this.commentService = commentService;
    }

    public GetArticleDTO getArticleById(String articleId) {

        DBArticle dbArticle = articleRepository.findById(articleId).orElseThrow(() ->
                new NoSuchElementException("Article not found with ID : " + articleId));
        return getArticleDTO(dbArticle);
    }

    public List<GetArticleDTO> getAllArticles() {
        List<DBArticle> articles = articleRepository.findAll();
        return articles
                .stream()
                .map(this::getArticleDTO)
                .toList();
    }

    public List<GetArticleDTO> getArticlesByThemeId(String themeId) {
        List<DBArticle> articles = articleRepository.findByThemeId(themeId);
        return articles
                .stream()
                .map(this::getArticleDTO)
                .toList();
    }

    public List<GetArticleDTO> getArticlesSorted(String sortOder) {
        Sort sort  = sortOder.equals("asc")
                ? Sort.by(Sort.Direction.ASC, "createdAt")
                : Sort.by(Sort.Direction.DESC, "createdAt");
        List<DBArticle> articles = articleRepository.findAll(sort);
        return articles
                .stream()
                .map(this::getArticleDTO)
                .toList();
    }

    public GetArticleDTO createArticle(CreateArticleDTO articleDTO, String authorEmail) {
        DBArticle article = modelMapper.map(articleDTO, DBArticle.class);

        GetUserDTO user = userService.getUserByEmail(authorEmail);
        article.setAuthorId(user.getId());
        article.setDate(new Date());
        article.setComments(new ArrayList<>());

        DBArticle savedArticle = articleRepository.save(article);
        return getArticleDTO(savedArticle);
    }

    public void deleteArticle(String articleId) {
        articleRepository
                .findById(articleId)
                .orElseThrow(() ->
                        new NoSuchElementException("Article not found with ID : " + articleId));
        articleRepository.deleteById(articleId);
    }

    private GetArticleDTO getArticleDTO(DBArticle article) {
        GetArticleDTO articleDTO = modelMapper.map(article, GetArticleDTO.class);
        GetUserDTO author = userService.getUserById(article.getAuthorId());
        articleDTO.setAuthor(author);
        GetThemeDTO theme = themeService.getThemeById(article.getThemeId());
        articleDTO.setTheme(theme);
        if(article.getComments() != null && !article.getComments().isEmpty()) {
            List<GetCommentDTO> comments = article.getComments()
                    .stream()
                    .map(commentService::getCommentById)
                    .toList();
            articleDTO.setComments(comments);
        } else {
            articleDTO.setComments(new ArrayList<>());
        }
        return articleDTO;
    }
}
