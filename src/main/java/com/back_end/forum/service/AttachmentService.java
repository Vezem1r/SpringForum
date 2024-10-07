package com.back_end.forum.service;

import com.back_end.forum.model.Attachment;
import com.back_end.forum.repository.AttachmentRepository;
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

    private final String UPLOAD_DIR = "src/main/resources/static/uploads";

    public Attachment saveAttachment(MultipartFile attachmentFile) throws IOException {
        String originalFilename = attachmentFile.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path uploadPath = Paths.get(UPLOAD_DIR, uniqueFilename);
        Files.write(uploadPath, attachmentFile.getBytes());
        Attachment attachment = new Attachment();
        attachment.setFilename(originalFilename);
        attachment.setSize(attachmentFile.getSize());
        attachment.setContentType(attachmentFile.getContentType());
        attachment.setFilePath(uploadPath.toString());
        attachment.setCreatedAt(LocalDateTime.now());
        return attachmentRepository.save(attachment);
    }

    public Attachment getAttachment(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
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
