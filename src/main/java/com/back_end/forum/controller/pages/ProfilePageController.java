package com.back_end.forum.controller.pages;

import com.back_end.forum.dto.UserProfileDto;
import com.back_end.forum.model.User;
import com.back_end.forum.responses.TopicResponseDto;
import com.back_end.forum.service.TopicService;
import com.back_end.forum.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/profilepage")
@AllArgsConstructor
public class ProfilePageController {

    private final TopicService topicService;
    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable String username,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size){

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

        Page<TopicResponseDto> userTopics = topicService.getUserTopicsByUsername(username, page, size);
        profileDto.setTopics(userTopics.getContent());

        // responce for pagination
        Map<String, Object> response = new HashMap<>();
        response.put("profile", profileDto);
        response.put("totalPages", userTopics.getTotalPages());
        response.put("totalElements", userTopics.getTotalElements());

        return ResponseEntity.ok(response);
    }

}
