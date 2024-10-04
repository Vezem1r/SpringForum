package com.back_end.forum.controller;

import com.back_end.forum.responses.TopicResponseDto;
import com.back_end.forum.model.Category;
import com.back_end.forum.service.CategoryService;
import com.back_end.forum.service.CommentService;
import com.back_end.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/homepage")
@RequiredArgsConstructor
public class HomePageController {

    private final TopicService topicService;
    private final CategoryService categoryService;


    @GetMapping("/getAllTopics")
    public Page<TopicResponseDto> getAllTopics(Pageable pageable) {
        return topicService.getAllTopics(pageable);
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/search")
    public Page<TopicResponseDto> searchTopics(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Integer maxRating,
            @RequestParam(required = false) String sortOrder,
            Pageable pageable) {

        List<String> tagList = (tags != null && !tags.isEmpty()) ? Arrays.asList(tags.split(",")) : Collections.emptyList();

        String[] sortParams = sortOrder.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

        System.out.println("Sorting by: " + sortField + " in direction: " + sortDirection);

        return topicService.searchTopics(title, category, tagList, minRating, maxRating, sortField, sortDirection, pageable);
    }
}
