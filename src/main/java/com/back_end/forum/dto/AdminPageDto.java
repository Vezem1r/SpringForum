package com.back_end.forum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminPageDto {
    private long totalUsers;
    private long loggedInTodayUsers;
    private long totalTopics;
    private long topicsCreatedToday;
    private long totalComments;
    private long commentsCreatedToday;
}
