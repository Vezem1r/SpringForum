package com.back_end.forum.service;

import com.back_end.forum.model.Comment;
import com.back_end.forum.model.Rating;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import com.back_end.forum.model.enums.NotificationType;
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

    @Autowired
    private NotificationService notificationService;

    public String rateTopic(Long topicId, Long userId, int value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        Optional<Rating> existingRating = ratingRepository.findByUserAndTopic(user, topic);

        int previousTopicRating = topic.getRating();
        User notUser = topic.getUser();

        if (existingRating.isPresent()) {
            Rating rating = existingRating.get();
            int previousValue = rating.getValue();
            rating.setValue(value);
            ratingRepository.save(rating);

            topic.setRating(topic.getRating() - previousValue + value);
            topicRepository.save(topic);

            int newTopicRating = topic.getRating();

            if(!user.equals(notUser) && previousTopicRating != newTopicRating) {
                notificationService.createNotification(notUser.getUsername(), user.getUsername(),"Rated your topic, new value: " + topic.getRating(), NotificationType.LIKE, topicId);
            }
            return "Rating updated successfully";
        } else {
            Rating rating = new Rating();
            rating.setUser(user);
            rating.setTopic(topic);
            rating.setValue(value);
            ratingRepository.save(rating);

            topic.setRating(topic.getRating() + value);
            topicRepository.save(topic);

            if(!user.equals(notUser)) {
                notificationService.createNotification(notUser.getUsername(), user.getUsername(),"Rated your topic, new value: " + topic.getRating(), NotificationType.LIKE, topicId);
            }
            return "Rating added successfully";
        }

    }

    public String rateComment(Long commentId, Long userId, int value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Optional<Rating> existingRating = ratingRepository.findByUserAndComment(user, comment);
        User notUser = comment.getUser();

        int previousCommentRating = comment.getRating();

        if (existingRating.isPresent()) {
            Rating rating = existingRating.get();
            int previousValue = rating.getValue();
            rating.setValue(value);
            ratingRepository.save(rating);

            comment.setRating(comment.getRating() - previousValue + value);
            commentRepository.save(comment);
            int newCommentRating = comment.getRating();
            if(!user.equals(notUser) && previousCommentRating!=newCommentRating) {
                notificationService.createNotification(notUser.getUsername(), user.getUsername(),"Rated your comment: " + comment.getContent() +", new value: " + comment.getRating(), NotificationType.LIKE, comment.getTopic().getId());
            }
            return "Rating updated successfully";
        }
        Rating rating = new Rating();
        rating.setUser(user);
        rating.setComment(comment);
        rating.setValue(value);
        ratingRepository.save(rating);

        comment.setRating(comment.getRating() + value);
        commentRepository.save(comment);

        if(!user.equals(notUser)) {
            notificationService.createNotification(notUser.getUsername(), user.getUsername(),"Rated your comment: " + comment.getContent() +", new value: " + comment.getRating(), NotificationType.LIKE, comment.getTopic().getId());
        }
        return "Rating added successfully";
    }
}

