package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.CreateCommentDTO;
import com.example.mdd_backend.dtos.GetCommentDTO;
import com.example.mdd_backend.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<GetCommentDTO> createComment(@Valid @RequestBody CreateCommentDTO commentDTO, @RequestParam String articleId, Authentication authentication) {
        if (commentDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String authorId = authentication.getName();
        GetCommentDTO createdComment = commentService.createComment(commentDTO, articleId, authorId);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GetCommentDTO>> getAllComments() {
        List<GetCommentDTO> comments = commentService.getAllComments();

        if (comments == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetCommentDTO> getCommentById(@PathVariable String id) {
        GetCommentDTO comment = commentService.getCommentById(id);

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
