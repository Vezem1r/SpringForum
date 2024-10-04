package com.back_end.forum.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {
    private String content;
    private String username;
    private Long topicId;
    private Long parentId;
    private LocalDateTime createdAt;
    private List<MultipartFile> attachments;
}
