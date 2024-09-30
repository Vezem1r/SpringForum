package com.back_end.forum.service;

import com.back_end.forum.model.Comment;
import com.back_end.forum.model.Rating;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import com.back_end.forum.repository.CommentRepository;
import com.back_end.forum.repository.RatingRepository;
import com.back_end.forum.repository.TopicRepository;
import com.back_end.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CommentRepository commentRepository;

    public String rateTopic(Long topicId, Long userId, int value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        Optional<Rating> existingRating = ratingRepository.findByUserAndTopic(user, topic);

        if (existingRating.isPresent()) {
            Rating rating = existingRating.get();
            int previousValue = rating.getValue();
            rating.setValue(value);
            ratingRepository.save(rating);

            topic.setRating(topic.getRating() - previousValue + value);
            topicRepository.save(topic);
            return "Rating updated successfully";
        } else {
            Rating rating = new Rating();
            rating.setUser(user);
            rating.setTopic(topic);
            rating.setValue(value);
            ratingRepository.save(rating);

            topic.setRating(topic.getRating() + value);
            topicRepository.save(topic);
            return "Rating added successfully";
        }

    }

    public String rateComment(Long commentId, Long userId, int value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Optional<Rating> existingRating = ratingRepository.findByUserAndComment(user, comment);

        if (existingRating.isPresent()) {
            Rating rating = existingRating.get();
            int previousValue = rating.getValue();
            rating.setValue(value);
            ratingRepository.save(rating);

            comment.setRating(comment.getRating() - previousValue + value);
            commentRepository.save(comment);
            return "Rating updated successfully";
        } else {
            Rating rating = new Rating();
            rating.setUser(user);
            rating.setComment(comment);
            rating.setValue(value);
            ratingRepository.save(rating);

            comment.setRating(comment.getRating() + value);
            commentRepository.save(comment);
            return "Rating added successfully";
        }
    }
}

