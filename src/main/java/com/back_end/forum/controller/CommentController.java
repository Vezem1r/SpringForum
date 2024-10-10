package com.back_end.forum.controller;

import com.back_end.forum.dto.CommentDto;
import com.back_end.forum.model.Comment;
import com.back_end.forum.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<Comment> createTopic(@ModelAttribute CommentDto commentDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Comment createdComment = commentService.addComment(commentDto, username);
            log.info("Comment successfully created by user {}", username);
            return ResponseEntity.ok(createdComment);
        } catch (IOException e) {
            log.error("Error occurred while creating comment for user {}", username, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
