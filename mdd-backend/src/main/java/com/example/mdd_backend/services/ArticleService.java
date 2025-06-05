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

/**
 * Service for managing articles.
 *
 * Handles article creation, retrieval, deletion and sorting operations.
 */
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

    /**
     * Retrieves an article by its unique identifier.
     * Includes author, topic and comments details.
     *
     * @param articleId The unique identifier of the article
     * @return Complete article with metadata
     * @throws ResourceNotFoundException If article doesn't exist
     * @throws BusinessLogicException On system error
     */
    public ArticleResponseDTO getArticleById(String articleId) {
        try {
            DBArticle dbArticle = articleRepository
                .findById(articleId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Article not found with ID : " + articleId
                    )
                );
            return ArticleResponseDTO(dbArticle);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving article with ID: {}", articleId, e);
            throw new BusinessLogicException("Failed to retrieve article");
        }
    }

    /**
     * Retrieves all articles sorted by specified criteria.
     *
     * @param sortKey Sort criteria (date, title, etc.)
     * @return List of articles sorted by specified key
     * @throws BusinessLogicException If sort type unsupported or system error
     */
    public List<ArticleResponseDTO> getArticlesSorted(String sortKey) {
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

            return articles.stream().map(this::ArticleResponseDTO).toList();
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

    /**
     * Creates a new article.
     *
     * @param articleDTO Article data to create
     * @param authorEmail Email of the article author
     * @return Created article with generated ID
     * @throws ResourceNotFoundException If author doesn't exist
     * @throws BusinessLogicException On creation failure
     */
    public ArticleResponseDTO createArticle(
        ArticleCreateRequestDTO articleDTO,
        String authorEmail
    ) {
        try {
            DBArticle article = modelMapper.map(articleDTO, DBArticle.class);

            UserResponseDTO user = userService.getUserByEmail(authorEmail);
            article.setAuthorId(user.getId());
            article.setCreatedAt(new Date());

            DBArticle savedArticle = articleRepository.save(article);

            return ArticleResponseDTO(savedArticle);
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

    /**
     * Deletes an article by ID.
     *
     * @param articleId The unique identifier of the article to delete
     * @throws ResourceNotFoundException If article doesn't exist
     * @throws BusinessLogicException On deletion failure
     */
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

    /**
     * Maps database article to response DTO.
     * Enriches with author, topic and comments data.
     *
     * @param article Database article entity
     * @return Complete article response DTO
     * @throws BusinessLogicException On mapping failure
     */
    private ArticleResponseDTO ArticleResponseDTO(DBArticle article) {
        try {
            ArticleResponseDTO articleDTO = modelMapper.map(
                article,
                ArticleResponseDTO.class
            );

            UserResponseDTO author = userService.getUserById(
                article.getAuthorId()
            );
            articleDTO.setAuthor(author);

            TopicResponseDTO topic = topicService.getTopicById(
                article.getTopicId()
            );
            articleDTO.setTopic(topic);

            List<CommentResponseDTO> comments =
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
