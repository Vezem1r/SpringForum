package com.back_end.forum.controller.admin;

import com.back_end.forum.model.Tag;
import com.back_end.forum.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TagService tagService;

    @DeleteMapping("/tag/delete/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable Long id) {
        Tag deletedTag = tagService.deleteTag(id);
        return ResponseEntity.ok("Tag has been removed");
    }
}
