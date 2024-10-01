package com.back_end.forum.controller;

import com.back_end.forum.dto.ChangePasswordDto;
import com.back_end.forum.dto.ChangeUsernameDto;
import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.dto.UserProfileDto;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import com.back_end.forum.responses.LoginResponse;
import com.back_end.forum.service.UserService;
import com.back_end.forum.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtService jwtService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> userProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        UserProfileDto profile = userService.getUserProfile(currentUser.getUserId());
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/change-username")
    public ResponseEntity<LoginResponse> changeUsername(@RequestBody ChangeUsernameDto changeUsernameDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (changeUsernameDto.getNewUsername() == null || changeUsernameDto.getNewUsername().isEmpty()) {
            return ResponseEntity.badRequest().body(new LoginResponse(null, "Username cannot be empty."));
        }

        boolean isChanged = userService.changeUsername(currentUser.getUserId(), changeUsernameDto.getNewUsername());
        if (isChanged) {
            currentUser.setUsername(changeUsernameDto.getNewUsername());
            String newToken = jwtService.generateToken(currentUser);
            LoginResponse loginResponse = new LoginResponse(newToken, jwtService.getExpirationTime(), "Username successfully changed.");
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.badRequest().body(new LoginResponse(null, "Username is already taken."));
        }
    }


    @GetMapping("/{userId}/topics")
    public ResponseEntity<List<Topic>> getUserTopics(@PathVariable Long userId) {
        List<Topic> topics = userService.getUserTopics(userId);
        return ResponseEntity.ok(topics);
    }
}
