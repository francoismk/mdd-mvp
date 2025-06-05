package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.CommentCreateRequestDTO;
import com.example.mdd_backend.dtos.CommentResponseDTO;
import com.example.mdd_backend.dtos.UserResponseDTO;
import com.example.mdd_backend.errors.exceptions.BusinessLogicException;
import com.example.mdd_backend.errors.exceptions.DatabaseOperationException;
import com.example.mdd_backend.errors.exceptions.ResourceNotFoundException;
import com.example.mdd_backend.models.DBArticle;
import com.example.mdd_backend.models.DBComment;
import com.example.mdd_backend.repositories.ArticleRepository;
import com.example.mdd_backend.repositories.CommentRepository;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for managing article comments.
 *
 * Handles comment creation, retrieval and deletion operations.
 */
@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(
        CommentService.class
    );

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public CommentService(
        CommentRepository commentRepository,
        ModelMapper modelMapper,
        UserService userService,
        ArticleRepository articleRepository
    ) {
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.articleRepository = articleRepository;
    }

    /**
     * Retrieves a comment by its unique identifier.
     *
     * @param id The unique identifier of the comment
     * @return Comment with author details
     * @throws ResourceNotFoundException If comment doesn't exist
     * @throws NoSuchElementException On retrieval failure
     */
    public CommentResponseDTO getCommentById(String id) {
        try {
            return commentRepository
                .findById(id)
                .map(this::CommentResponseDTO)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Comment not found with ID: " + id
                    )
                );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving comment with ID: {}", id, e);
            throw new NoSuchElementException("Failed to retrieve comment");
        }
    }

    /**
     * Retrieves all comments in the system.
     *
     * @return List of all comments with author details
     * @throws DatabaseOperationException On retrieval failure
     */
    public List<CommentResponseDTO> getAllComments() {
        try {
            return commentRepository
                .findAll()
                .stream()
                .map(this::CommentResponseDTO)
                .toList();
        } catch (Exception e) {
            logger.error(
                "Error retrieving all comments: {}",
                e.getMessage(),
                e
            );
            throw new DatabaseOperationException("Failed to retrieve comments");
        }
    }

    /**
     * Creates a new comment on an article.
     *
     * @param createCommentDTO Comment data to create
     * @param articleId The article to comment on
     * @param authorEmail Email of the comment author
     * @return Created comment with generated ID
     * @throws ResourceNotFoundException If article or author doesn't exist
     * @throws DatabaseOperationException On creation failure
     */
    public CommentResponseDTO createComment(
        CommentCreateRequestDTO createCommentDTO,
        String articleId,
        String authorEmail
    ) {
        try {
            DBArticle article = articleRepository
                .findById(articleId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Article not found with ID: " + articleId
                    )
                );

            DBComment comment = modelMapper.map(
                createCommentDTO,
                DBComment.class
            );

            UserResponseDTO user = userService.getUserByEmail(authorEmail);

            comment.setArticleId(articleId);
            comment.setAuthorId(user.getId());
            comment.setCreatedAt(new Date());
            DBComment savedComment = commentRepository.save(comment);

            return CommentResponseDTO(savedComment);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error(
                "Error creating comment for article {}: {}",
                articleId,
                e.getMessage(),
                e
            );
            throw new DatabaseOperationException("Failed to create comment");
        }
    }

    /**
     * Deletes a comment by ID.
     *
     * @param id The unique identifier of the comment to delete
     * @throws ResourceNotFoundException If comment doesn't exist
     * @throws DatabaseOperationException On deletion failure
     */
    public void deleteComment(String id) {
        try {
            commentRepository
                .findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Comment not found with ID: " + id
                    )
                );
            commentRepository.deleteById(id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error(
                "Error deleting comment with ID {}: {}",
                id,
                e.getMessage(),
                e
            );
            throw new DatabaseOperationException("Failed to delete comment");
        }
    }

    /**
     * Maps database comment to response DTO.
     * Enriches with author data.
     *
     * @param comment Database comment entity
     * @return Comment response DTO with author details
     * @throws BusinessLogicException On mapping failure
     */
    private CommentResponseDTO CommentResponseDTO(DBComment comment) {
        try {
            CommentResponseDTO commentDTO = modelMapper.map(
                comment,
                CommentResponseDTO.class
            );
            commentDTO.setAuthor(
                userService.getUserById(comment.getAuthorId())
            );
            return commentDTO;
        } catch (ResourceNotFoundException e) {
            logger.error(
                "Author not found for comment {}: {}",
                comment.getId(),
                e.getMessage(),
                e
            );
            throw new BusinessLogicException(
                "Failed to retrieve comment author"
            );
        } catch (Exception e) {
            logger.error(
                "Error mapping comment DTO for comment {}: {}",
                comment.getId(),
                e.getMessage(),
                e
            );
            throw new BusinessLogicException("Failed to map comment to DTO");
        }
    }

    /**
     * Retrieves all comments for a specific article.
     *
     * @param articleId The unique identifier of the article
     * @return List of comments for the specified article
     * @throws DatabaseOperationException On retrieval failure
     */
    public List<CommentResponseDTO> getCommentsByArticleId(String articleId) {
        try {
            return commentRepository
                .findByArticleId(articleId)
                .stream()
                .map(this::CommentResponseDTO)
                .toList();
        } catch (Exception e) {
            logger.error(
                "Error retrieving comments for article {}: {}",
                articleId,
                e.getMessage(),
                e
            );
            throw new DatabaseOperationException(
                "Failed to retrieve comments for article"
            );
        }
    }
}
