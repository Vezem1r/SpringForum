package com.back_end.forum.controller;

import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.model.Attachment;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import com.back_end.forum.repository.UserRepository;
import com.back_end.forum.service.AttachmentService;
import com.back_end.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Topic>> getTopicsByCategory(@RequestParam Long categoryId) {
        List<Topic> topics = topicService.getTopicsByCategory(categoryId);
        if (topics.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(topics);
    }

    @PostMapping("/create")
    public ResponseEntity<Topic> createTopic(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("tagNames") List<String> tagNames,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        TopicDto topicDto = new TopicDto();
        topicDto.setTitle(title);
        topicDto.setContent(content);
        topicDto.setUserId(user.getUserId());
        topicDto.setCategoryId(categoryId);
        topicDto.setTagNames(tagNames);

        Topic createdTopic = topicService.createTopic(topicDto, attachment);
        return ResponseEntity.ok(createdTopic);
    }


}
