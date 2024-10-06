package com.back_end.forum.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class TopicPageResponse {
    private Long topicId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
    private String category;
    private List<String> tagNames;
    private Integer rating;
    private String bannerUrl;
    private List<AttachmentResponse> attachments;
    private List<CommentResponse> comments;
}