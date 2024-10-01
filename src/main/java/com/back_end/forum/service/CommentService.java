package com.back_end.forum.service;

import com.back_end.forum.dto.CommentDto;
import com.back_end.forum.model.*;
import com.back_end.forum.repository.AttachmentRepository;
import com.back_end.forum.repository.CommentRepository;
import com.back_end.forum.repository.TopicRepository;
import com.back_end.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final AttachmentService attachmentService;
    private final NotificationService notificationService;

    public Comment createComment(CommentDto commentDto, MultipartFile[] attachments) throws IOException {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        User user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        comment.setUser(user);

        Topic topic = topicRepository.findById(commentDto.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        comment.setTopic(topic);

        if (commentDto.getParentId() != null) {
            Comment parentComment = commentRepository.findById(commentDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parentComment);

            notificationService.createNotification(
                    parentComment.getUser().getUserId(),
                    "You have a new reply to your comment in the topic: " + topic.getTitle()
            );
        }

        Comment savedComment = commentRepository.save(comment);

        List<Attachment> savedAttachments = new ArrayList<>();
        if (attachments != null) {
            for (MultipartFile attachment : attachments) {
                savedAttachments.add(attachmentService.saveAttachment(attachment, null, savedComment.getCommentId()));
            }
        }
        savedComment.setAttachments(savedAttachments);


        Notification notification = notificationService.createNotification(
                topic.getUser().getUserId(),
                "You have a new comment on your topic: " + topic.getTitle()
        );

        return savedComment;
    }

    public Page<CommentDto> getCommentsByTopicId(Long topicId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByTopic_TopicId(topicId, pageable);
        return comments.map(this::convertToDto);
    }
    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setContent(comment.getContent());
        dto.setUsername(comment.getUser().getUsername());

        // Проверка на null для родительского комментария
        if (comment.getParentComment() != null) {
            dto.setParentId(comment.getParentComment().getCommentId());
        } else {
            dto.setParentId(null); // или можно не устанавливать это поле
        }

        dto.setCreatedAt(comment.getCreatedAt());
        dto.setTopicId(comment.getTopic().getTopicId());
        dto.setAttachmentIds(comment.getAttachments().stream()
                .map(Attachment::getId)
                .collect(Collectors.toList()));

        return dto;
    }

}
