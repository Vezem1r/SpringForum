package com.back_end.forum.controller;

import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import com.back_end.forum.repository.UserRepository;
import com.back_end.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final UserRepository userRepository;

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
