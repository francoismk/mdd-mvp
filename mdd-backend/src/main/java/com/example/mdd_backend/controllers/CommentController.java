package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.CommentCreateRequestDTO;
import com.example.mdd_backend.dtos.CommentResponseDTO;
import com.example.mdd_backend.services.CommentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

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

    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getAllComments() {
        List<CommentResponseDTO> comments = commentService.getAllComments();

        if (comments == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCommentById(@PathVariable String id) {
        commentService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
