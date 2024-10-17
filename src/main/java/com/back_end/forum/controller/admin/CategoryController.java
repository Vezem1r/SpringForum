package com.back_end.forum.controller.admin;

import com.back_end.forum.dto.CategoryDto;
import com.back_end.forum.model.Category;
import com.back_end.forum.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/category")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto){
        Category createdCategory = categoryService.createCategory(categoryDto);
        log.info("Category created: {}", createdCategory);
        return ResponseEntity.ok(createdCategory);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto){
        Category updatedCategory = categoryService.updateCategory(id, categoryDto);
        log.info("Category updated: {}", updatedCategory);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        log.info("Category deleted: {}", id);
        return ResponseEntity.ok("Category has been removed");
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        log.info("Category list: {}", categories);
        return ResponseEntity.ok(categories);
    }

}
