package com.back_end.forum.service;

import com.back_end.forum.model.Notification;
import com.back_end.forum.model.User;
import com.back_end.forum.model.enums.NotificationType;
import com.back_end.forum.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createNotification(String recipientUsername, String actorUsername, String message, NotificationType notificationType, Long topicId) {
        Notification notification = new Notification();

        notification.setRecipientUsername(recipientUsername);
        notification.setActorUsername(actorUsername);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setTopicId(topicId);
        notification.setRead(false);
        notification.setTimestamp(LocalDateTime.now());

        log.info("Creating notification: {}", notification);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(User recipient) {
        log.info("Fetching notifications for user: {}", recipient.getUsername());
        return notificationRepository.findByRecipientUsername(recipient.getUsername());
    }

    public Notification getNotificationById(Long id) {
        log.info("Fetching notification by id: {}", id);
        return notificationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Notification not found for id: {}", id);
                    return new RuntimeException("Notification not found");
                });
    }

    public void markAsRead(Long notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.error("Notification not found for id: {}", notificationId);
                    return new RuntimeException("Notification not found");
                });
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void deleteNotification(Long notificationId) {
        log.info("Deleting notification: {}", notificationId);
        notificationRepository.deleteById(notificationId);
    }

    public void markAllAsReadForUser(String recipientUsername) {
        log.info("Marking all notifications as read for user: {}", recipientUsername);
        List<Notification> notifications = notificationRepository.findByRecipientUsername(recipientUsername);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }

    public void deleteAllNotificationsForUser(String recipientUsername) {
        log.info("Deleting all notifications for user: {}", recipientUsername);
        List<Notification> notifications = notificationRepository.findByRecipientUsername(recipientUsername);
        notificationRepository.deleteAll(notifications);
    }
}

