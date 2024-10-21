package com.back_end.forum.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "banners")
public class Banner {

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
}