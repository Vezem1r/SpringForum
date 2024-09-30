package com.back_end.forum.service;

import com.back_end.forum.dto.CategoryDto;
import com.back_end.forum.exception.BadRequest;
import com.back_end.forum.model.Category;
import com.back_end.forum.repository.CategoryRepository;
import com.back_end.forum.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TopicRepository topicRepository;

    public Category createCategory(CategoryDto categoryDto){
        Optional<Category> optionalCategory = categoryRepository.findByName(categoryDto.getName());

        if(optionalCategory.isPresent()){
            throw new BadRequest("Category already exists");
        }

        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        return categoryRepository.save(category);
    }

    public Category deleteCategory(Long id){
        Category category =  categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequest("Category not found with id " + id));
        categoryRepository.delete(category);
        return category;
    }

    public Category updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequest("Category not found with id " + id));
        Optional<Category> existingCategory = categoryRepository.findByName(categoryDto.getName());

        if(existingCategory.isPresent()) throw new BadRequest("Category name already taken");

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        return categoryRepository.save(category);
    }
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

}
