package com.back_end.forum.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "content", length = 4095, nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "user_user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "topic_topic_id", nullable = false)
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private Set<Comment> replies;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Attachment> attachments;

    @Column(nullable = false)
    private Integer rating = 0;

    public void updateRating(int value) {
        this.rating += value;
    }
}
