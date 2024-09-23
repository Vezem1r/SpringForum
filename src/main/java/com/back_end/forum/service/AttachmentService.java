package com.back_end.forum.service;

import com.back_end.forum.model.Attachment;
import com.back_end.forum.repository.AttachmentRepository;
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

    public Attachment saveAttachment(MultipartFile file, Long topicId, Long commentId) throws IOException {
        Attachment attachment = new Attachment();
        attachment.setFilename(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setCreatedAt(LocalDateTime.now());

        //if it is topic we have to rezise immage to use it after as topic banner
        if (topicId != null) {
            byte[] resizedImage = ImageUtils.resizeAndCompressImage(file);
            attachment.setData(resizedImage);
            attachment.setTopicId(topicId);
        } else {
            //if it is comment
            attachment.setData(file.getBytes());
            attachment.setCommentId(commentId);
        }

        return attachmentRepository.save(attachment);
    }

    public Attachment getAttachment(Long id) {
        return attachmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Attachment not found"));
    }
}
