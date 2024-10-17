package com.back_end.forum.controller.admin;

import com.back_end.forum.dto.TagDto;
import com.back_end.forum.model.Tag;
import com.back_end.forum.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/tag")
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

    @PutMapping("/update/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable Long id, @RequestBody TagDto tagDto) {
        log.info("Updating a tag with id: {}", id);
        Tag updatedTag = tagService.updateTag(id, tagDto);
        log.info("Tag '{}' has been updated", updatedTag.getName());
        return ResponseEntity.ok(updatedTag);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        log.info("Tag '{}' has been deleted", id);
        return ResponseEntity.ok("Tag has been removed");
    }

    @GetMapping("/getAllTags")
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        log.info("Tags found: {}", tags);
        return ResponseEntity.ok(tags);
    }
}
