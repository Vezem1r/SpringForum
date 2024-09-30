package com.back_end.forum.service;

import com.back_end.forum.model.Attachment;
import com.back_end.forum.model.Comment;
import com.back_end.forum.model.Topic;
import com.back_end.forum.repository.AttachmentRepository;
import com.back_end.forum.repository.CommentRepository;
import com.back_end.forum.repository.TopicRepository;
import com.back_end.forum.utils.ImageUtils;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TopicRepository topicRepository;
    private final CommentRepository commentRepository;

    private final String uploadDir = "src/main/resources/static/uploads";

    public Attachment saveAttachment(MultipartFile file, Long topicId, Long commentId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path uploadPath = Paths.get(uploadDir, uniqueFilename);

        if(topicId != null){
            byte[] resizedImage = ImageUtils.resizeAndCompressImage(file);
            Files.write(uploadPath,resizedImage);
        }
            else{
                Files.write(uploadPath, file.getBytes());

        }
        Attachment attachment = new Attachment();
        attachment.setFilename(originalFilename);
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setFilePath(uploadPath.toString());
        attachment.setCreatedAt(LocalDateTime.now());

        System.out.println("Starts saving");
        if (topicId != null) {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic not found"));
            System.out.println("Setting topic to attachment" + topic);
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

    public byte[] getFileContent(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    public Resource loadAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        try {
            byte[] data = attachment.getData();
            return new ByteArrayResource(data);
        } catch (IOException e) {
            throw new RuntimeException("Could not read file", e);
        }
    }
}
