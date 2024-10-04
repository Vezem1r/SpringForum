package com.back_end.forum.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class TopicResponseDto {
    private Long topicId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
    private String category;
    private List<String> tagNames;
    private Integer rating;
}
