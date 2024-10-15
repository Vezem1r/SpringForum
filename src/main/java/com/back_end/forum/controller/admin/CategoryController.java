package com.back_end.forum.controller.admin;

import com.back_end.forum.dto.CategoryDto;
import com.back_end.forum.model.Category;
import com.back_end.forum.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/category")
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto){
        Category createdCategory = categoryService.createCategory(categoryDto);
        System.out.println("Category has been created");
        return ResponseEntity.ok(createdCategory);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto){
        Category updatedCategory = categoryService.updateCategory(id, categoryDto);
        System.out.println("Category has been updated");
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category has been removed");
    }


}
