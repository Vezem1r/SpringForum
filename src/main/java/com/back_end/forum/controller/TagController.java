package com.back_end.forum.controller;

import com.back_end.forum.dto.CategoryDto;
import com.back_end.forum.dto.TagDto;
import com.back_end.forum.model.Category;
import com.back_end.forum.model.Tag;
import com.back_end.forum.service.CategoryService;
import com.back_end.forum.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/tag")
@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping("/create")
    public ResponseEntity<Tag> createTag(@RequestBody TagDto tagDto){
        Tag createdTag = tagService.createTag(tagDto);
        System.out.println("Tag has been created");
        return ResponseEntity.ok(createdTag);
    }
}
