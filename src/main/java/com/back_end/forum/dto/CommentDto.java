package com.back_end.forum.dto;

import lombok.Data;
import java.util.List;

@Data
public class CommentDto {
    private String content;
    private Long userId;
    private Long topicId;
    private Long parentId;
    private List<Long> attachmentIds;
}
