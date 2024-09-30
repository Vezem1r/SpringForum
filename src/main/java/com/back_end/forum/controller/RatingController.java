package com.back_end.forum.controller;

import com.back_end.forum.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/topic/{topicId}")
    public ResponseEntity<String> rateTopic(@PathVariable Long topicId,
                                            @RequestParam Long userId,
                                            @RequestParam int value) {
        String response = ratingService.rateTopic(topicId, userId, value);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<String> rateComment(@PathVariable Long commentId,
                                              @RequestParam Long userId,
                                              @RequestParam int value) {
        String response = ratingService.rateComment(commentId, userId, value);
        return ResponseEntity.ok(response);
    }
}