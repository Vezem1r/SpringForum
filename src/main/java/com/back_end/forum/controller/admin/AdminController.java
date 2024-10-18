package com.back_end.forum.controller.admin;

import com.back_end.forum.dto.AdminPageDto;
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



    //TODO Edit(with rating), delete, topic.

    //TODO Edit(with rating), delete, comments.

}
