package com.back_end.forum.controller;

import com.back_end.forum.model.User;
import com.back_end.forum.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/topic/{topicId}")
    public ResponseEntity<String> rateTopic(@PathVariable Long topicId,
                                            @RequestParam int value) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Long userId = currentUser.getUserId();

        String response = ratingService.rateTopic(topicId, userId, value);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<String> rateComment(@PathVariable Long commentId,
                                              @RequestParam int value) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Long userId = currentUser.getUserId();

        String response = ratingService.rateComment(commentId, userId, value);
        return ResponseEntity.ok(response);
    }
}