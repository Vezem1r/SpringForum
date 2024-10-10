package com.back_end.forum.controller.pages;

import com.back_end.forum.dto.CommentDto;
import com.back_end.forum.model.Attachment;
import com.back_end.forum.responses.CommentResponse;
import com.back_end.forum.responses.TopicPageResponse;
import com.back_end.forum.service.AttachmentService;
import com.back_end.forum.service.CommentService;
import com.back_end.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topicpage")
@RequiredArgsConstructor
@Slf4j
public class TopicPageController {

    private final AttachmentService attachmentService;
    private final TopicService topicService;
    private final CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<TopicPageResponse> getTopicPage(@PathVariable Long id, @PageableDefault(size = 5) Pageable pageable) {
        log.info("Fetching topic page for topicId: {}", id);
        TopicPageResponse topicPageResponse = topicService.getTopicPageById(id, pageable);
        log.info("Successfully fetched topic page for topicId: {}", id);
        return ResponseEntity.ok(topicPageResponse);
    }

    @GetMapping("/comments/{commentId}/rating")
    public ResponseEntity<Integer> getCommentRating(@PathVariable Long commentId) {
        log.info("Fetching rating for commentId: {}", commentId);
        Integer rating = commentService.getCommentRating(commentId);
        log.info("Successfully fetched rating for commentId: {}", commentId);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<Page<CommentResponse>> getCommentReplies(@PathVariable Long commentId, @PageableDefault(size = 4) Pageable pageable) {
        log.info("Fetching replies for commentId: {}", commentId);
        Page<CommentResponse> replies = commentService.getCommentReplies(commentId, pageable);
        log.info("Successfully fetched replies for commentId: {}", commentId);
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/attachments/download/{attachmentId}")
    public ResponseEntity<Resource> downloadCommentAttachment(@PathVariable Long attachmentId) {
        log.info("Downloading attachment with attachmentId: {}", attachmentId);
        try {
            Attachment attachment = attachmentService.getAttachment(attachmentId);
            String filename = attachment.getFilename();
            Resource resource = attachmentService.loadAttachment(attachmentId);
            log.info("Successfully downloaded attachment with filename: {}", filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (RuntimeException e) {
            log.error("Failed to download attachment with attachmentId: {}", attachmentId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/topics/{topicId}/attachments/download/{attachmentId}")
    public ResponseEntity<Resource> downloadTopicAttachment(@PathVariable Long topicId, @PathVariable Long attachmentId) {
        log.info("Downloading topic attachment with topicId: {} and attachmentId: {}", topicId, attachmentId);
        try {
            Attachment attachment = attachmentService.getAttachment(attachmentId);
            String filename = attachment.getFilename();
            Resource resource = attachmentService.loadAttachment(attachmentId);
            log.info("Successfully downloaded topic attachment with filename: {}", filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (RuntimeException e) {
            log.error("Failed to download topic attachment with topicId: {} and attachmentId: {}", topicId, attachmentId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/attachments/{id}")
    public ResponseEntity<Resource> getAttachment(@PathVariable Long id) {
        log.info("Fetching attachment with id: {}", id);
        Resource resource = attachmentService.loadAttachment(id);
        log.info("Successfully fetched attachment with id: {}", id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
