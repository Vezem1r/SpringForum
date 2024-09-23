package com.back_end.forum.dto;

import lombok.Data;

import java.util.List;

@Data
public class TopicDto {
    private String title;
    private String content;
    private Long userId;
    private Long categoryId;
    private List<String> tagNames;
    private Long attachmentId;
}
