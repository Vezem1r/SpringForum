package com.back_end.forum.repository;

import com.back_end.forum.model.Rating;
import com.back_end.forum.model.User;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndTopic(User user, Topic topic);
    Optional<Rating> findByUserAndComment(User user, Comment comment);
}
