package com.back_end.forum.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(nullable = false)
    private Integer value; // Decrement for downvote, increment for upvote

    public Rating(User user, Topic topic, Comment comment, Integer value) {
        this.user = user;
        this.topic = topic;
        this.comment = comment;
        this.value = value;
    }
}
