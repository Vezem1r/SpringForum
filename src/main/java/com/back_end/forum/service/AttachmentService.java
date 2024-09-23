package com.back_end.forum.service;

import com.back_end.forum.model.Attachment;
import com.back_end.forum.model.Comment; // Импортируем ваш собственный класс Comment
import com.back_end.forum.model.Topic;
import com.back_end.forum.repository.AttachmentRepository;
import com.back_end.forum.repository.CommentRepository;
import com.back_end.forum.repository.TopicRepository;
import com.back_end.forum.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TopicRepository topicRepository;
    private final CommentRepository commentRepository;

    public Attachment saveAttachment(MultipartFile file, Long topicId, Long commentId) throws IOException {
        Attachment attachment = new Attachment();
        attachment.setFilename(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setCreatedAt(LocalDateTime.now());

        byte[] resizedImage = ImageUtils.resizeAndCompressImage(file);
        attachment.setData(resizedImage);

        if (topicId != null) {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic not found"));
            attachment.setTopic(topic);
        } else if (commentId != null) {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new RuntimeException("Comment not found"));
            attachment.setComment(comment);
        }

        return attachmentRepository.save(attachment);
    }

    public Attachment getAttachment(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
    }
}
