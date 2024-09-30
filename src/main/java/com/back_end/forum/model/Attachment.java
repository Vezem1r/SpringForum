package com.back_end.forum.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String contentType;
    private Long size;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "topic_id")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public byte[] getData() throws IOException {
        Path path = Path.of(filePath);
        return Files.readAllBytes(path);
    }
}
