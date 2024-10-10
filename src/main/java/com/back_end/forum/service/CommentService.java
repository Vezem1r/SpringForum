package com.back_end.forum.service;

import com.back_end.forum.dto.CommentDto;
import com.back_end.forum.model.*;
import com.back_end.forum.model.enums.NotificationType;
import com.back_end.forum.repository.AttachmentRepository;
import com.back_end.forum.repository.CommentRepository;
import com.back_end.forum.repository.TopicRepository;
import com.back_end.forum.repository.UserRepository;
import com.back_end.forum.responses.AttachmentResponse;
import com.back_end.forum.responses.CommentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;
    private final NotificationService notificationService;

    public Comment addComment(CommentDto commentDto, String username) throws IOException {
        log.info("Adding comment for topic id: {}", commentDto.getTopicId());
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        comment.setUser(user);
        Topic topic = topicRepository.findById(commentDto.getTopicId())
                .orElseThrow(() -> {
                    log.error("Topic not found with id: {}", commentDto.getTopicId());
                    return new RuntimeException("Topic not found");
                });
        topic.setUpdatedAt(LocalDateTime.now());
        topicRepository.save(topic);
        comment.setTopic(topic);
        comment.setParentComment(commentDto.getParentId() != null ? findCommentById(commentDto.getParentId()) : null);

        if(commentDto.getParentId() != null) {
            Optional<Comment> notificationComment = commentRepository.findById(commentDto.getParentId());
            User notificationUser = notificationComment.get().getUser();
            if (!user.equals(notificationUser)) {
                notificationService.createNotification(notificationUser.getUsername(), user.getUsername(), "Replied on your comment.", NotificationType.REPLY, topic.getId());
            }
        } else if (!topic.getUser().equals(user)) {
            notificationService.createNotification(topic.getUser().getUsername(), user.getUsername(), "Commented your topic.",NotificationType.COMMENT, topic.getId());
        }

        Comment savedComment = commentRepository.save(comment);

        if (commentDto.getAttachments() != null) {
            for (MultipartFile attachmentFile : commentDto.getAttachments()) {
                if (attachmentFile != null && !attachmentFile.isEmpty()) {
                    log.info("Saving attachment for comment id: {}", savedComment.getCommentId());
                    Attachment attachment = attachmentService.saveAttachment(attachmentFile);
                    if (attachment != null) {
                        attachment.setComment(savedComment);
                        attachmentRepository.save(attachment);
                        comment.addAttachment(attachment);
                    }
                }
            }
        }
        return savedComment;
    }

    private Comment findCommentById(Long id) {
        log.info("Finding comment by id: {}", id);
        return commentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Comment not found with id: {}", id);
                    return new RuntimeException("Comment not found");
                });
    }

    public Page<CommentResponse> getCommentReplies(Long parentId, Pageable pageable) {
        log.info("Fetching replies for comment id: {}", parentId);
        Page<Comment> parentComments = commentRepository.findByParentComment_CommentId(parentId, pageable);
        return parentComments.map(comment -> {
            CommentResponse dto = new CommentResponse(
                    comment.getCommentId(),
                    comment.getContent(),
                    comment.getCreatedAt(),
                    comment.getUser().getUsername(),
                    parentId,
                    comment.getRating(),
                    commentRepository.countByParentComment_CommentId(comment.getCommentId()),
                    new ArrayList<>(),
                    new ArrayList<>()
            );

            List<CommentResponse> childReplies = loadChildReplies(comment.getCommentId());
            dto.setReplies(childReplies);

            List<AttachmentResponse> attachments = loadAttachmentsForComment(comment);
            dto.setAttachments(attachments);

            return dto;
        });
    }

    private List<AttachmentResponse> loadAttachmentsForComment(Comment comment) {
        log.info("Loading attachments for comment id: {}", comment.getCommentId());
        return comment.getAttachments().stream()
                .map(attachment -> new AttachmentResponse(
                        attachment.getId(),
                        attachment.getFilename(),
                        attachment.getFilePath()
                ))
                .collect(Collectors.toList());
    }

    private List<CommentResponse> loadChildReplies(Long parentId) {
        log.info("Loading child replies for comment id: {}", parentId);
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<Comment> replies = commentRepository.findByParentComment_CommentId(parentId, pageable).getContent();

        return replies.stream().map(reply -> {
            CommentResponse replyDto = new CommentResponse(
                    reply.getCommentId(),
                    reply.getContent(),
                    reply.getCreatedAt(),
                    reply.getUser().getUsername(),
                    parentId,
                    reply.getRating(),
                    commentRepository.countByParentComment_CommentId(reply.getCommentId()),
                    new ArrayList<>(),
                    new ArrayList<>()
            );

            List<CommentResponse> childReplies = loadChildReplies(reply.getCommentId());
            replyDto.setReplies(childReplies);

            List<AttachmentResponse> attachments = loadAttachmentsForComment(reply);
            replyDto.setAttachments(attachments);

            return replyDto;
        }).collect(Collectors.toList());
    }

    public Integer getCommentRating(Long commentId) {
        log.info("Getting rating for comment id: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return comment.getRating();
    }
}
