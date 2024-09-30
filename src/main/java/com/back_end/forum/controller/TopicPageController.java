package com.back_end.forum.controller;

import com.back_end.forum.service.AttachmentService;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/topicpage")
@RequiredArgsConstructor
public class TopicPageController {

    private final AttachmentService attachmentService;

    @GetMapping("/attachments/{id}")
    public ResponseEntity<Resource> getAttachment(@PathVariable Long id) {
        Resource resource = attachmentService.loadAttachment(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
