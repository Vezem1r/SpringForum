package com.back_end.forum.controller;

import com.back_end.forum.model.Attachment;
import com.back_end.forum.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/upload")
    public ResponseEntity<Attachment> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "topicId", required = false) Long topicId,
            @RequestParam(value = "commentId", required = false) Long commentId) {
        System.out.println("Received file: " + file.getOriginalFilename());
        System.out.println("Topic ID: " + topicId);
        System.out.println("Comment ID: " + commentId);
        try {
            Attachment attachment = attachmentService.saveAttachment(file, topicId, commentId);
            return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<byte[]> getAttachmentFile(@PathVariable Long id) {
        Attachment attachment = attachmentService.getAttachment(id);
        try {
            byte[] fileContent = attachmentService.getFileContent(attachment.getFilePath());
            return ResponseEntity.ok(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
