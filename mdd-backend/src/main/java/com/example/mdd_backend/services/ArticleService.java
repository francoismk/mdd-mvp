package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.*;
import com.example.mdd_backend.models.DBArticle;
import com.example.mdd_backend.repositories.ArticleRepository;
import com.example.mdd_backend.services.articleSorting.ArticleSortStrategy;
import com.example.mdd_backend.services.articleSorting.SortType;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    private final ArticleRepository articleRepository;
    private final List<ArticleSortStrategy> sortStrategies;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final TopicService topicService;
    private final CommentService commentService;

    public ArticleService(ArticleRepository articleRepository, ModelMapper modelMapper, UserService userService, TopicService topicService, CommentService commentService, List<ArticleSortStrategy> sortStrategies) {
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.topicService = topicService;
        this.commentService = commentService;
        this.sortStrategies = sortStrategies;
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
        List<DBArticle> articles = articleRepository.findByTopicId(themeId);
        return articles
                .stream()
                .map(this::getArticleDTO)
                .toList();
    }

    public List<GetArticleDTO> getArticlesSorted(String sortKey) {
        SortType sortType = SortType.fromString(sortKey);

        ArticleSortStrategy strategy = sortStrategies
                .stream()
                .filter(s -> s.supports(sortType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported sort type: " + sortKey));

        List<DBArticle> articles = articleRepository.findAll(strategy.getSort());

        return articles
                .stream()
                .map(this::getArticleDTO)
                .toList();
    }

    public GetArticleDTO createArticle(CreateArticleDTO articleDTO, String authorEmail) {
        DBArticle article = modelMapper.map(articleDTO, DBArticle.class);

        GetUserDTO user = userService.getUserByEmail(authorEmail);
        article.setAuthorId(user.getId());
        article.setCreatedAt(new Date());


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

        GetTopicDTO topic = topicService.getTopicById(article.getTopicId());
        articleDTO.setTopic(topic);

        List<GetCommentDTO> comments = commentService.getCommentsByArticleId(article.getId());
        articleDTO.setComments(comments);

        return articleDTO;
    }
}
