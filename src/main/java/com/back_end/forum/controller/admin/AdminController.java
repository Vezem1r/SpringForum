package com.back_end.forum.controller.admin;

import com.back_end.forum.dto.CategoryDto;
import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.model.Category;
import com.back_end.forum.model.Tag;
import com.back_end.forum.service.TagService;
import com.back_end.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TagService tagService;
    private final TopicService topicService;

    @DeleteMapping("/tag/delete/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable Long id){
        Tag deletedTag =  tagService.deleteTag(id);
        return ResponseEntity.ok("Tag has been removed");
    }

//    @PutMapping("/topic/update/{id}")
//    public ResponseEntity<Category> updateTopic(@PathVariable Long id, @RequestBody TopicDto topicDto){
//        Category updatedCategory = topicService.updateTopic(id, topicDto);
//        System.out.println("Category has been updated");
//        return ResponseEntity.ok(updatedCategory);
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteTopic(@PathVariable Long id){
//        Category deletedCategory =  topicService.deleteTopic(id);
//        return ResponseEntity.ok("Category has been removed");
}
