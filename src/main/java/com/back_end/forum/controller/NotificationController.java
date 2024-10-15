package com.back_end.forum.controller;

import com.back_end.forum.model.Notification;
import com.back_end.forum.model.User;
import com.back_end.forum.repository.UserRepository;
import com.back_end.forum.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(Principal principal) {
        log.info("Fetching notifications for user: {}", principal.getName());
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> {
                    log.error("User not found: {}", principal.getName());
                    return new RuntimeException("User not found");
                });

        List<Notification> notifications = notificationService.getNotificationsForUser(user);
        log.info("Fetched {} notifications for user: {}", notifications.size(), user.getUsername());
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{id}/mark-as-read")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Principal principal) {
        log.info("Marking notification {} as read for user: {}", id, principal.getName());
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> {
                    log.error("User not found: {}", principal.getName());
                    return new RuntimeException("User not found");
                });

        Notification notification = notificationService.getNotificationById(id);
        if (notification.getRecipientUsername().equals(user.getUsername())) {
            notificationService.markAsRead(id);
            log.info("Notification {} marked as read for user: {}", id, user.getUsername());
            return ResponseEntity.ok().build();
        } else {
            log.warn("User {} tried to mark notification {} that does not belong to them", user.getUsername(), id);
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id, Principal principal) {
        log.info("Deleting notification {} for user: {}", id, principal.getName());
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> {
                    log.error("User not found: {}", principal.getName());
                    return new RuntimeException("User not found");
                });

        Notification notification = notificationService.getNotificationById(id);
        if (notification.getRecipientUsername().equals(user.getUsername())) {
            notificationService.deleteNotification(id);
            log.info("Notification {} deleted for user: {}", id, user.getUsername());
            return ResponseEntity.noContent().build();
        } else {
            log.warn("User {} tried to delete notification {} that does not belong to them", user.getUsername(), id);
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/mark-all-as-read")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<Void> markAllAsRead(Principal principal) {
        log.info("Marking all notifications as read for user: {}", principal.getName());
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> {
                    log.error("User not found: {}", principal.getName());
                    return new RuntimeException("User not found");
                });
        notificationService.markAllAsReadForUser(user.getUsername());
        log.info("All notifications marked as read for user: {}", user.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteAll")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Void> deleteAllNotifications(Principal principal) {
        log.info("Deleting all notifications for user: {}", principal.getName());
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> {
                    log.error("User not found: {}", principal.getName());
                    return new RuntimeException("User not found");
                });
        notificationService.deleteAllNotificationsForUser(user.getUsername());
        log.info("All notifications deleted for user: {}", user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
