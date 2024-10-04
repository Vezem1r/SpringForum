package com.back_end.forum.controller;

import com.back_end.forum.dto.CommentDto;
import com.back_end.forum.responses.TopicResponseDto;
import com.back_end.forum.dto.SearchRequest;
import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.dto.TopicWithAttachmentsDto;
import com.back_end.forum.model.Category;
import com.back_end.forum.model.Comment;
import com.back_end.forum.model.Topic;
import com.back_end.forum.service.CategoryService;
import com.back_end.forum.service.CommentService;
import com.back_end.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/homepage")
@RequiredArgsConstructor
public class HomePageController {

    private final TopicService topicService;
    private final CategoryService categoryService;
    private final CommentService commentService;


    @GetMapping("/getAllTopics")
    public Page<TopicResponseDto> getAllTopics(Pageable pageable) {
        return topicService.getAllTopics(pageable);
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Topic>> searchTopics(@RequestBody SearchRequest searchRequest) {
        List<Topic> topics = topicService.searchTopics(searchRequest);
        return ResponseEntity.ok(topics);
    }
}
