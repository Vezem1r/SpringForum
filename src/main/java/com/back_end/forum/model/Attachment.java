package com.back_end.forum.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import com.back_end.forum.model.Comment;

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
    @Lob
    private byte[] data; //file in db

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "attachment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
