package com.back_end.forum.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {
    private String content;
    private Long userId;
    private String username;
    private Long topicId;
    private Long parentId;
    private LocalDateTime createdAt;
    private List<Long> attachmentIds;
}
