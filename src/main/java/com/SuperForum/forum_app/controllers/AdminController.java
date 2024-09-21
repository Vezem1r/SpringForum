package com.SuperForum.forum_app.controllers;

import com.SuperForum.forum_app.dtos.CategoryDto;
import com.SuperForum.forum_app.services.admin.AdminService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/category")
    public ResponseEntity<CategoryDto> postCategory(@RequestBody CategoryDto categoryDto){
        CategoryDto createdCategoryDto = adminService.postCategory(categoryDto);
        System.out.println(categoryDto);
        if (createdCategoryDto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(createdCategoryDto);
    }

}
