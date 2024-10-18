package com.back_end.forum.service;

import com.back_end.forum.model.Attachment;
import com.back_end.forum.repository.AttachmentRepository;
import com.back_end.forum.utils.FolderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
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
@Slf4j
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    private final static String UPLOAD_DIR = "static/uploads";

    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;

        FolderUtils.createDirectories(UPLOAD_DIR);
    }

    public Attachment saveAttachment(MultipartFile attachmentFile) throws IOException {
        String originalFilename = attachmentFile.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
        Path uploadPath = Paths.get(UPLOAD_DIR, uniqueFilename);

        log.info("Saving attachment: {} to path: {}", originalFilename, uploadPath);

        Files.write(uploadPath, attachmentFile.getBytes());

        Attachment attachment = new Attachment();
        attachment.setFilename(originalFilename);
        attachment.setSize(attachmentFile.getSize());
        attachment.setContentType(attachmentFile.getContentType());
        attachment.setFilePath(uploadPath.toString());
        attachment.setCreatedAt(LocalDateTime.now());

        log.info("Attachment saved successfully: {}", attachment);

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
