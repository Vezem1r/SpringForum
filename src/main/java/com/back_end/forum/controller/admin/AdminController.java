package com.back_end.forum.controller.admin;

import com.back_end.forum.dto.AdminPageDto;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import com.back_end.forum.service.UserService;
import com.back_end.forum.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<AdminPageDto> getAdminDashboard() {
        AdminPageDto adminPageDto = adminService.getAdminPage();
        log.info(adminPageDto.toString());
        return ResponseEntity.ok(adminPageDto);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.allUsers();
        log.info(users.toString());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestParam(required = false) String username,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        try {
            User updatedUser = adminService.updateUserProfile(userId, username, avatar);
            log.info("User updated: {}", updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while processing the request: " + e.getMessage());
        }
    }

    @PutMapping("/topics/{topicId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateTopic(
            @PathVariable Long topicId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile banner,
            @RequestParam(required = false) Integer rating) {
        try {
            Topic updatedTopic = adminService.updateTopic(topicId, title, content, banner, rating);
            log.info("Topic updated: {}", updatedTopic);
            return ResponseEntity.ok(updatedTopic);
        } catch (Exception e) {
            log.error("Error updating topic: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while processing the request: " + e.getMessage());
        }
    }

    @DeleteMapping("/topics/{topicId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteTopic(@PathVariable Long topicId) {
        try {
            adminService.deleteTopic(topicId);
            log.info("Topic deleted with id: {}", topicId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting topic: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while processing the request: " + e.getMessage());
        }
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            adminService.deleteComment(commentId);
            log.info("Comment deleted with id: {}", commentId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting comment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while processing the request: " + e.getMessage());
        }
    }
}
