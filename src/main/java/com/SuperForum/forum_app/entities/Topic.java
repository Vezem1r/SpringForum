package com.SuperForum.forum_app.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long topicId;

    @Column(name = "title", length = 1023, nullable = false)
    private String title;

    @Column(name = "content", length = 4095, nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "user_user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "categories_category_id", nullable = false)
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "topics_tags",
            joinColumns = @JoinColumn(name = "topics_topic_id"),
            inverseJoinColumns = @JoinColumn(name = "tags_tag_id")
    )
    private Set<Tag> tags;
}
