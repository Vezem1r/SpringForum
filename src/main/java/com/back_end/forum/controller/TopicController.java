package com.back_end.forum.controller;

import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.model.Topic;
import com.back_end.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
@Slf4j
public class TopicController {

    private final TopicService topicService;

    @PostMapping("/create")
    public ResponseEntity<Topic> createTopic(@ModelAttribute TopicDto topicDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        log.info("User '{}' is attempting to create a new topic with title: '{}'", username, topicDto.getTitle());

        try {
            Topic createdTopic = topicService.createTopic(topicDto, username);
            log.info("Topic '{}' has been successfully created by user '{}'", createdTopic.getTitle(), username);
            return ResponseEntity.ok(createdTopic);
        } catch (IOException e) {
            log.error("Error occurred while creating the topic: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
