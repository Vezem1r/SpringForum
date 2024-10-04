package com.back_end.forum.controller;

import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import com.back_end.forum.repository.UserRepository;
import com.back_end.forum.service.AttachmentService;
import com.back_end.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;

    @PostMapping("/create")
    public ResponseEntity<Topic> createTopic(@ModelAttribute TopicDto topicDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Topic createdTopic = topicService.createTopic(topicDto, username);
            return ResponseEntity.ok(createdTopic);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
