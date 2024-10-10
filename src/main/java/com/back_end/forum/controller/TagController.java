package com.back_end.forum.controller;

import com.back_end.forum.dto.TagDto;
import com.back_end.forum.model.Tag;
import com.back_end.forum.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/tag")
@RestController
@RequiredArgsConstructor
@Slf4j
public class TagController {

    private final TagService tagService;

    @PostMapping("/create")
    public ResponseEntity<Tag> createTag(@RequestBody TagDto tagDto) {
        log.info("Creating a new tag with name: {}", tagDto.getName());
        Tag createdTag;
        try {
            createdTag = tagService.createTag(tagDto);
            log.info("Tag '{}' has been successfully created", createdTag.getName());
        } catch (Exception e) {
            log.error("Error occurred while creating tag: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(createdTag);
    }
}
