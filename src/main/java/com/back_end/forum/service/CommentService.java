package com.back_end.forum.service;

import com.back_end.forum.dto.CommentDto;
import com.back_end.forum.model.*;
import com.back_end.forum.repository.AttachmentRepository;
import com.back_end.forum.repository.CommentRepository;
import com.back_end.forum.repository.TopicRepository;
import com.back_end.forum.repository.UserRepository;
import com.back_end.forum.responses.CommentResponse;
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

    public Comment addComment(CommentDto commentDto, String username) throws IOException {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        comment.setUser(user);
        Topic topic = topicRepository.findById(commentDto.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        topic.setUpdatedAt(LocalDateTime.now());
        topicRepository.save(topic);
        comment.setTopic(topic);
        comment.setParentComment(commentDto.getParentId() != null ? findCommentById(commentDto.getParentId()) : null);

        Comment savedComment = commentRepository.save(comment);

        if (commentDto.getAttachments() != null) {
            for (MultipartFile attachmentFile : commentDto.getAttachments()) {
                Attachment attachment = attachmentService.saveAttachment(attachmentFile);
                comment.addAttachment(attachment);
            }
        }
        return savedComment;
    }

    private Comment findCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public Page<CommentResponse> getCommentReplies(Long parentId, Pageable pageable) {
        Page<Comment> parentComments = commentRepository.findByParentComment_CommentId(parentId, pageable);
        return parentComments.map(comment -> {
            CommentResponse dto = new CommentResponse(
                    comment.getCommentId(),
                    comment.getContent(),
                    comment.getCreatedAt(),
                    comment.getUser().getUsername(),
                    parentId,
                    commentRepository.countByParentComment_CommentId(comment.getCommentId()),
                    new ArrayList<>()
            );

            List<CommentResponse> childReplies = loadChildReplies(comment.getCommentId());
            dto.setReplies(childReplies);

            return dto;
        });
    }

    private List<CommentResponse> loadChildReplies(Long parentId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<Comment> replies = commentRepository.findByParentComment_CommentId(parentId, pageable).getContent();

        return replies.stream().map(reply -> {
            CommentResponse replyDto = new CommentResponse(
                    reply.getCommentId(),
                    reply.getContent(),
                    reply.getCreatedAt(),
                    reply.getUser().getUsername(),
                    parentId,
                    commentRepository.countByParentComment_CommentId(reply.getCommentId()),
                    new ArrayList<>()
            );

            List<CommentResponse> childReplies = loadChildReplies(reply.getCommentId());
            replyDto.setReplies(childReplies);

            return replyDto;
        }).collect(Collectors.toList());
    }
}
