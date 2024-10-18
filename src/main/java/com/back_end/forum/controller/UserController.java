package com.back_end.forum.controller;

import com.back_end.forum.dto.ChangeUsernameDto;
import com.back_end.forum.model.User;
import com.back_end.forum.responses.LoginResponse;
import com.back_end.forum.service.UserService;
import com.back_end.forum.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/upload-avatar")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<String> uploadAvatar(@RequestParam("avatar") MultipartFile avatar) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        log.info("User '{}' is attempting to upload an avatar.", currentUser.getUsername());

        try {
            String avatarFileName = userService.uploadAvatar(currentUser.getUserId(), avatar);
            log.info("Avatar uploaded successfully for user '{}'. File name: {}", currentUser.getUsername(), avatarFileName);
            return ResponseEntity.ok("Avatar uploaded successfully: " + avatarFileName);
        } catch (IOException e) {
            log.error("Failed to upload avatar for user '{}'. Error: {}", currentUser.getUsername(), e.getMessage());
            return ResponseEntity.status(500).body("Failed to upload avatar.");
        }
    }

    @PostMapping("/change-username")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<LoginResponse> changeUsername(@RequestBody ChangeUsernameDto changeUsernameDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (changeUsernameDto.getNewUsername() == null || changeUsernameDto.getNewUsername().isEmpty()) {
            log.warn("User '{}' attempted to change username to an empty value.", currentUser.getUsername());
            return ResponseEntity.badRequest().body(new LoginResponse(null, "Username cannot be empty."));
        }

        log.info("User '{}' is attempting to change their username to '{}'.", currentUser.getUsername(), changeUsernameDto.getNewUsername());

        boolean isChanged = userService.changeUsername(currentUser.getUserId(), changeUsernameDto.getNewUsername());
        if (isChanged) {
            currentUser.setUsername(changeUsernameDto.getNewUsername());
            String newToken = jwtService.generateToken(currentUser);
            log.info("Username successfully changed to '{}' for user '{}'.", changeUsernameDto.getNewUsername(), currentUser.getUsername());
            LoginResponse loginResponse = new LoginResponse(newToken, jwtService.getExpirationTime(), "Username successfully changed.");
            return ResponseEntity.ok(loginResponse);
        } else {
            log.warn("Failed to change username to '{}' for user '{}'. Username is already taken.", changeUsernameDto.getNewUsername(), currentUser.getUsername());
            return ResponseEntity.badRequest().body(new LoginResponse(null, "Username is already taken."));
        }
    }
}
