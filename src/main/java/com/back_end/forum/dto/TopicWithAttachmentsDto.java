package com.back_end.forum.dto;

import com.back_end.forum.model.Attachment;
import com.back_end.forum.model.User;
import com.back_end.forum.model.Category;
import com.back_end.forum.model.Tag;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TopicWithAttachmentsDto {
    private Long topicId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User user;
    private Integer rating;
    private Category category;
    private List<Tag> tags;
    private List<Attachment> attachments;
}
