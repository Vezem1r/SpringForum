package com.back_end.forum.controller.pages;

import com.back_end.forum.dto.CommentDto;
import com.back_end.forum.model.Attachment;
import com.back_end.forum.model.Comment;
import com.back_end.forum.repository.CommentRepository;
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
    public ResponseEntity<TopicPageResponse> getTopicPage(@PathVariable Long id, @PageableDefault(size = 5) Pageable pageable){
        TopicPageResponse topicPageResponse = topicService.getTopicPageById(id, pageable);
        return ResponseEntity.ok(topicPageResponse);
    }

    @GetMapping("/comments/{commentId}/rating")
    public ResponseEntity<Integer> getCommentRating(@PathVariable Long commentId) {
        Integer rating = commentService.getCommentRating(commentId);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<Page<CommentResponse>> getCommentReplies(@PathVariable Long commentId, @PageableDefault(size = 4) Pageable pageable){
        Page<CommentResponse> replies = commentService.getCommentReplies(commentId, pageable);
        return ResponseEntity.ok(replies);
    }
    @GetMapping("/attachments/download/{attachmentId}")
    public ResponseEntity<Resource> downloadCommentAttachment(@PathVariable Long attachmentId) {
        try {
            Attachment attachment = attachmentService.getAttachment(attachmentId);
            String filename = attachment.getFilename();
            Resource resource = attachmentService.loadAttachment(attachmentId);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @GetMapping("/topics/{topicId}/attachments/download/{attachmentId}")
    public ResponseEntity<Resource> downloadTopicAttachment(@PathVariable Long topicId, @PathVariable Long attachmentId) {
        try {
            Attachment attachment = attachmentService.getAttachment(attachmentId);
            String filename = attachment.getFilename();
            Resource resource = attachmentService.loadAttachment(attachmentId);

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
