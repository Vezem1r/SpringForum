package com.back_end.forum.controller;

import com.back_end.forum.model.Attachment;
import com.back_end.forum.responses.CommentResponse;
import com.back_end.forum.responses.TopicPageResponse;
import com.back_end.forum.service.AttachmentService;
import com.back_end.forum.service.CommentService;
import com.back_end.forum.service.TopicService;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/topicpage")
@RequiredArgsConstructor
public class TopicPageController {

    private final AttachmentService attachmentService;
    private final TopicService topicService;
    private final CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<TopicPageResponse> getTopicPage(@PathVariable Long id, @PageableDefault(size = 10) Pageable pageable){
        TopicPageResponse topicPageResponse = topicService.getTopicPageById(id, pageable);
        return ResponseEntity.ok(topicPageResponse);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<Page<CommentResponse>> getCommentReplies(@PathVariable Long commentId, Pageable pageable){
        Page<CommentResponse> replies = commentService.getCommentReplies(commentId, pageable);
        return ResponseEntity.ok(replies);
    }
    @GetMapping("/attachments/download/{id}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id) {
        try {
            Attachment attachment = attachmentService.getAttachment(id);
            String filename = attachment.getFilename();
            //String filePath = attachment.getFilePath();

            Resource resource = attachmentService.loadAttachment(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @GetMapping("/attachments/{id}")
    public ResponseEntity<Resource> getAttachment(@PathVariable Long id) {
        Resource resource = attachmentService.loadAttachment(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }


}
