package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.CreateCommentDTO;
import com.example.mdd_backend.dtos.GetCommentDTO;
import com.example.mdd_backend.dtos.GetUserDTO;
import com.example.mdd_backend.models.DBArticle;
import com.example.mdd_backend.models.DBComment;
import com.example.mdd_backend.repositories.ArticleRepository;
import com.example.mdd_backend.repositories.CommentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, ModelMapper modelMapper, UserService userService, ArticleRepository articleRepository) {
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.articleRepository = articleRepository;
    }

    public GetCommentDTO getCommentById(String id) {
        return commentRepository
            .findById(id)
            .map(this::getCommentDTO)
            .orElseThrow(() -> new NoSuchElementException("Comment not found with ID: " + id));
    }

    public List<GetCommentDTO> getAllComments() {
        return commentRepository
            .findAll()
            .stream()
            .map(this::getCommentDTO)
            .toList();
    }

    public GetCommentDTO createComment(CreateCommentDTO createCommentDTO, String articleId, String authorEmail) {
        DBArticle article = articleRepository.findById(articleId).orElseThrow(
            () -> new NoSuchElementException("Article not found with ID: " + articleId)
        );

        DBComment comment = modelMapper.map(createCommentDTO, DBComment.class);

        GetUserDTO user = userService.getUserByEmail(authorEmail);

        comment.setArticleId(articleId);
        comment.setAuthorId(user.getId());
        comment.setDate(new Date());
        DBComment savedComment = commentRepository.save(comment);

        if(article.getComments() == null) {
            article.setComments(new ArrayList<>());
        }
        article.getComments().add(savedComment.getId());
        articleRepository.save(article);
        return getCommentDTO(savedComment);
    }

    public void deleteComment(String id) {
        commentRepository
            .findById(id)
            .orElseThrow(() -> new NoSuchElementException("Comment not found with ID: " + id));
        commentRepository.deleteById(id);
    }

    private GetCommentDTO getCommentDTO(DBComment comment) {
        GetCommentDTO commentDTO = modelMapper.map(comment, GetCommentDTO.class);
        commentDTO.setAuthor(userService.getUserById(comment.getAuthorId()));
        return commentDTO;
    }


}
