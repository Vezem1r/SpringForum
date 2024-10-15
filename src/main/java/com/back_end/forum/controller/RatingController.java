package com.back_end.forum.controller;

import com.back_end.forum.model.User;
import com.back_end.forum.service.RatingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
@AllArgsConstructor
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/topic/{topicId}")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<String> rateTopic(@PathVariable Long topicId,
                                            @RequestParam int value) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Long userId = currentUser.getUserId();

        log.info("User {} is rating topic {} with value {}", userId, topicId, value);
        String response;
        try {
            response = ratingService.rateTopic(topicId, userId, value);
            log.info("User {} successfully rated topic {}", userId, topicId);
        } catch (Exception e) {
            log.error("Error rating topic {} by user {}: {}", topicId, userId, e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to rate topic");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/comment/{commentId}")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<String> rateComment(@PathVariable Long commentId,
                                              @RequestParam int value) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Long userId = currentUser.getUserId();

        log.info("User {} is rating comment {} with value {}", userId, commentId, value);
        String response;
        try {
            response = ratingService.rateComment(commentId, userId, value);
            log.info("User {} successfully rated comment {}", userId, commentId);
        } catch (Exception e) {
            log.error("Error rating comment {} by user {}: {}", commentId, userId, e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to rate comment");
        }
        return ResponseEntity.ok(response);
    }
}
