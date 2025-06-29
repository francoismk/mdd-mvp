package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.CommentCreateRequestDTO;
import com.example.mdd_backend.dtos.CommentResponseDTO;
import com.example.mdd_backend.services.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "Comment management operations")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Creates a new comment for a specific article.
     *
     * @param commentDTO The DTO containing the comment's information. Must be a valid object.
     * @param articleId  The ID of the article to which the comment is associated.
     * @param authentication The authentication object representing the user.
     * @return ResponseEntity containing the created CommentResponseDTO if successful,
     *         or an HTTP 400 Bad Request status if the commentDTO is null.
     */
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
        @Valid @RequestBody CommentCreateRequestDTO commentDTO,
        @RequestParam String articleId,
        Authentication authentication
    ) {
        if (commentDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String authorId = authentication.getName();
        CommentResponseDTO createdComment = commentService.createComment(
            commentDTO,
            articleId,
            authorId
        );
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    /**
     * Retrieves all comments.
     *
     * @return ResponseEntity containing a list of CommentResponseDTO if comments exist,
     *         or an HTTP 204 No Content status if no comments are found.
     */
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getAllComments() {
        List<CommentResponseDTO> comments = commentService.getAllComments();

        if (comments == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    /**
     * Retrieves a comment by its ID.
     *
     * @param id The ID of the comment to retrieve.
     * @return ResponseEntity containing the CommentResponseDTO if found,
     *         or an HTTP 404 Not Found status if the comment does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> getCommentById(
        @PathVariable String id
    ) {
        CommentResponseDTO comment = commentService.getCommentById(id);

        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    /**
     * Deletes a comment by its ID.
     *
     * @param id The ID of the comment to delete.
     * @return ResponseEntity with an HTTP 200 OK status upon successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCommentById(@PathVariable String id) {
        commentService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
