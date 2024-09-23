package com.back_end.forum.controller;

import com.back_end.forum.dto.CommentDto;
import com.back_end.forum.model.Comment;
import com.back_end.forum.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<Comment> createComment(
            @RequestParam("content") String content,
            @RequestParam("userId") Long userId,
            @RequestParam("topicId") Long topicId,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments) throws IOException {

        CommentDto commentDto = new CommentDto();
        commentDto.setContent(content);
        commentDto.setUserId(userId);
        commentDto.setTopicId(topicId);
        commentDto.setParentId(parentId);

        Comment createdComment = commentService.createComment(commentDto, attachments);
        return ResponseEntity.ok(createdComment);
    }
}
