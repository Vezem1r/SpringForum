package com.back_end.forum.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class TopicDto {
    private Long topicId;
    private String title;
    private String content;
    private String username;
    private Long categoryId;
    private List<String> tagNames;
    private List<MultipartFile> attachments;
    private MultipartFile banner;
}
