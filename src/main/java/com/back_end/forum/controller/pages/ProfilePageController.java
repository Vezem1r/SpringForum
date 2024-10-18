package com.back_end.forum.controller.pages;

import com.back_end.forum.dto.UserProfileDto;
import com.back_end.forum.model.User;
import com.back_end.forum.responses.TopicResponseDto;
import com.back_end.forum.service.TopicService;
import com.back_end.forum.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/profilepage")
@AllArgsConstructor
@Slf4j
public class ProfilePageController {

    private final TopicService topicService;
    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable String username,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size){
        log.info("Fetching profile for user: {}", username);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserProfileDto profileDto = userService.getProfileByUsername(username);

        log.info("Profile data loaded for user: {}", username);

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            log.info("Authenticated as: {}", currentUser.getUsername());
            if (currentUser.getUsername().equals(username)) {
                profileDto.setEmail(currentUser.getEmail());
                log.info("User is viewing their own profile, email added");
            } else {
                profileDto.setEmail(null);
                log.info("User is viewing someone else's profile, email hidden");
            }
        } else {
            //if no auth
            profileDto.setEmail(null);
            log.warn("User is not authenticated, email hidden");
        }

        Page<TopicResponseDto> userTopics = topicService.getUserTopicsByUsername(username, page, size);
        profileDto.setTopics(userTopics.getContent());

        // responce for pagination
        Map<String, Object> response = new HashMap<>();
        response.put("profile", profileDto);
        response.put("totalPages", userTopics.getTotalPages());
        response.put("totalElements", userTopics.getTotalElements());

        log.info("Profile page data for user {} successfully returned", username);
        return ResponseEntity.ok(response);
    }

}
