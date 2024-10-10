package com.back_end.forum.service;

import com.back_end.forum.model.Notification;
import com.back_end.forum.model.User;
import com.back_end.forum.model.enums.NotificationType;
import com.back_end.forum.repository.NotificationRepository;
import com.back_end.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void createNotification(String recipientUsername, String actorUsername, String message, NotificationType notificationType, Long topicId) {
        Notification notification = new Notification();
        notification.setRecipientUsername(recipientUsername);
        notification.setActorUsername(actorUsername);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setTopicId(topicId);
        notification.setRead(false);
        notification.setTimestamp(LocalDateTime.now());
        System.out.println(notification);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(User recipient) {
        return notificationRepository.findByRecipientUsername(recipient.getUsername());
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void markAllAsReadForUser(String recipientUsername) {
        List<Notification> notifications = notificationRepository.findByRecipientUsername(recipientUsername);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }

    public void deleteAllNotificationsForUser(String recipientUsername) {
        List<Notification> notifications = notificationRepository.findByRecipientUsername(recipientUsername);
        notificationRepository.deleteAll(notifications);
    }
}

