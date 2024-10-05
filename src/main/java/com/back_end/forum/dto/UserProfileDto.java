package com.back_end.forum.dto;

import com.back_end.forum.responses.TopicResponseDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserProfileDto {
    private Long userId;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private int commentCount;
    private int topicCount;
    private String profilePicture;
    private List<TopicResponseDto> topics;
}
