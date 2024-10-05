package com.back_end.forum.controller;

import com.back_end.forum.dto.UserProfileDto;
import com.back_end.forum.model.User;
import com.back_end.forum.responses.TopicResponseDto;
import com.back_end.forum.service.TopicService;
import com.back_end.forum.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/profilepage")
@AllArgsConstructor
public class ProfilePageController {

    private final TopicService topicService;
    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable String username){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserProfileDto profileDto = userService.getProfileByUsername(username);

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();

            if (currentUser.getUsername().equals(username)) {
                profileDto.setEmail(currentUser.getEmail());
            } else {
                profileDto.setEmail(null);
            }
        } else {
            //if no auth
            profileDto.setEmail(null);
        }

        List<TopicResponseDto> userTopics = topicService.getUserTopicsByUsername(username);
        profileDto.setTopics(userTopics);

        return ResponseEntity.ok(profileDto);
    }
}
