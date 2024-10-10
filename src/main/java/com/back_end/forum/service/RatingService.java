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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    public String rateTopic(Long topicId, Long userId, int value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new RuntimeException("User not found");
                });
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> {
                    log.error("Topic not found with id: {}", topicId);
                    return new RuntimeException("Topic not found");
                });

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
            log.info("Updated rating for topic {} by user {}. New rating: {}", topicId, userId, newTopicRating);

            if (!user.equals(notUser) && previousTopicRating != newTopicRating) {
                notificationService.createNotification(notUser.getUsername(), user.getUsername(),
                        "Rated your topic, new value: " + topic.getRating(), NotificationType.LIKE, topicId);
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

            log.info("Added new rating for topic {} by user {}. New rating: {}", topicId, userId, topic.getRating());

            if(!user.equals(notUser)) {
                notificationService.createNotification(notUser.getUsername(), user.getUsername(),"Rated your topic, new value: " + topic.getRating(), NotificationType.LIKE, topicId);
            }
            return "Rating added successfully";
        }

    }

    public String rateComment(Long commentId, Long userId, int value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new RuntimeException("User not found");
                });
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment not found with id: {}", commentId);
                    return new RuntimeException("Comment not found");
                });

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
            log.info("Updated rating for comment {} by user {}. New rating: {}", commentId, userId, newCommentRating);
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

        log.info("Added new rating for comment {} by user {}. New rating: {}", commentId, userId, comment.getRating());

        if(!user.equals(notUser)) {
            notificationService.createNotification(notUser.getUsername(), user.getUsername(),"Rated your comment: " + comment.getContent() +", new value: " + comment.getRating(), NotificationType.LIKE, comment.getTopic().getId());
        }
        return "Rating added successfully";
    }
}

