package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.*;
import com.example.mdd_backend.errors.exceptions.BusinessLogicException;
import com.example.mdd_backend.errors.exceptions.ResourceNotFoundException;
import com.example.mdd_backend.models.DBArticle;
import com.example.mdd_backend.repositories.ArticleRepository;
import com.example.mdd_backend.services.articleSorting.ArticleSortStrategy;
import com.example.mdd_backend.services.articleSorting.SortType;
import java.util.Date;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(
        ArticleService.class
    );

    private final ArticleRepository articleRepository;
    private final List<ArticleSortStrategy> sortStrategies;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final TopicService topicService;
    private final CommentService commentService;

    public ArticleService(
        ArticleRepository articleRepository,
        ModelMapper modelMapper,
        UserService userService,
        TopicService topicService,
        CommentService commentService,
        List<ArticleSortStrategy> sortStrategies
    ) {
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.topicService = topicService;
        this.commentService = commentService;
        this.sortStrategies = sortStrategies;
    }

    public GetArticleDTO getArticleById(String articleId) {
        try {
            DBArticle dbArticle = articleRepository
                .findById(articleId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Article not found with ID : " + articleId
                    )
                );
            return getArticleDTO(dbArticle);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving article with ID: {}", articleId, e);
            throw new BusinessLogicException("Failed to retrieve article");
        }
    }

    public List<GetArticleDTO> getArticlesSorted(String sortKey) {
        try {
            SortType sortType = SortType.fromString(sortKey);

            ArticleSortStrategy strategy = sortStrategies
                .stream()
                .filter(s -> s.supports(sortType))
                .findFirst()
                .orElseThrow(() ->
                    new BusinessLogicException(
                        "Unsupported sort type: " + sortKey
                    )
                );

            List<DBArticle> articles = articleRepository.findAll(
                strategy.getSort()
            );

            return articles.stream().map(this::getArticleDTO).toList();
        } catch (BusinessLogicException e) {
            throw e;
        } catch (Exception e) {
            logger.error(
                "Error retrieving articles with sort key: {}: {}",
                sortKey,
                e.getMessage()
            );
            throw new BusinessLogicException("Failed to retrieve articles");
        }
    }

    public GetArticleDTO createArticle(
        CreateArticleDTO articleDTO,
        String authorEmail
    ) {
        try {
            DBArticle article = modelMapper.map(articleDTO, DBArticle.class);

            GetUserDTO user = userService.getUserByEmail(authorEmail);
            article.setAuthorId(user.getId());
            article.setCreatedAt(new Date());

            DBArticle savedArticle = articleRepository.save(article);

            return getArticleDTO(savedArticle);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error(
                "Error creating article for user {}: {}",
                authorEmail,
                e.getMessage(),
                e
            );
            throw new BusinessLogicException("Failed to create article");
        }
    }

    public void deleteArticle(String articleId) {
        try {
            articleRepository
                .findById(articleId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Article not found with ID : " + articleId
                    )
                );
            articleRepository.deleteById(articleId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error(
                "Error deleting article with ID {}: {}",
                articleId,
                e.getMessage(),
                e
            );
            throw new BusinessLogicException("Failed to delete article");
        }
    }

    private GetArticleDTO getArticleDTO(DBArticle article) {
        try {
            GetArticleDTO articleDTO = modelMapper.map(
                article,
                GetArticleDTO.class
            );

            GetUserDTO author = userService.getUserById(article.getAuthorId());
            articleDTO.setAuthor(author);

            TopicResponseDTO topic = topicService.getTopicById(
                article.getTopicId()
            );
            articleDTO.setTopic(topic);

            List<GetCommentDTO> comments =
                commentService.getCommentsByArticleId(article.getId());
            articleDTO.setComments(comments);

            return articleDTO;
        } catch (ResourceNotFoundException e) {
            logger.error(
                "Resource not found while mapping article to DTO: {}",
                e.getMessage(),
                e
            );
            throw new BusinessLogicException(
                "Resource not found while mapping article to DTO"
            );
        } catch (Exception e) {
            logger.error(
                "Error mapping article DTO for article {}: {}",
                article.getId(),
                e.getMessage(),
                e
            );
            throw new BusinessLogicException("Failed to map article to DTO");
        }
    }
}
