package com.back_end.forum.controller;

import com.back_end.forum.model.Category;
import com.back_end.forum.model.Topic;
import com.back_end.forum.service.CategoryService;
import com.back_end.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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


    @GetMapping
    public ResponseEntity<Page<Topic>> getAllTopics(Pageable pageable) {
        Page<Topic> topics = topicService.getAllTopics(pageable);
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/getTopic/{topicId}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long topicId) {
        Optional<Topic> topic = topicService.getTopicById(topicId);
        if (topic.isPresent()) {
            return ResponseEntity.ok(topic.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Topic>> searchTopics(
            @RequestParam Optional<Long> categoryId,
            @RequestParam Optional<String> title,
            @RequestParam Optional<LocalDateTime> startDate,
            @RequestParam Optional<LocalDateTime> endDate,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<List<String>> tagNames) {

        List<Topic> topics = topicService.searchTopics(categoryId, title, startDate, endDate, sortBy, tagNames);
        return ResponseEntity.ok(topics);
    }
}
