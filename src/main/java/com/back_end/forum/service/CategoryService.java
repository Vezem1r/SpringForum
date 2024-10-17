package com.back_end.forum.service;

import com.back_end.forum.dto.CategoryDto;
import com.back_end.forum.exception.BadRequest;
import com.back_end.forum.model.Category;
import com.back_end.forum.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(CategoryDto categoryDto){
        log.info("Creating category: {}", categoryDto.getName());
        Optional<Category> optionalCategory = categoryRepository.findByName(categoryDto.getName());

        if(optionalCategory.isPresent()){
            log.error("Category already exists: {}", categoryDto.getName());
            throw new BadRequest("Category already exists");
        }

        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        return categoryRepository.save(category);
    }

    public Category deleteCategory(Long id){
        log.info("Deleting category with id: {}", id);
        Category category =  categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequest("Category not found with id " + id));
        categoryRepository.delete(category);
        return category;
    }

    public Category updateCategory(Long id, CategoryDto categoryDto) {
        log.info("Updating category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", id);
                    return new BadRequest("Category not found with id " + id);
                });
        Optional<Category> existingCategory = categoryRepository.findByName(categoryDto.getName());

        if (existingCategory.isPresent()) {
            log.error("Category name already taken: {}", categoryDto.getName());
            throw new BadRequest("Category name already taken");
        }

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        log.info("Fetching all categories");
        return categoryRepository.findAll();
    }

    public void createCategories(int amount) {

        List<String> predefinedCategoryNames = List.of(
                "Technology",
                "Science",
                "Movies",
                "Music",
                "Sports",
                "Programming",
                "Travel",
                "Health",
                "Education",
                "Gaming"
        );

        log.info("Creating {} categories", amount);

        if (amount > predefinedCategoryNames.size()) {
            throw new IllegalArgumentException("Cannot create more than " + predefinedCategoryNames.size() + " categories");
        }

        Random random = new Random();

        for (int i = 0; i < amount; i++) {
            String categoryName = predefinedCategoryNames.get(random.nextInt(predefinedCategoryNames.size()));

            if (categoryRepository.findByName(categoryName).isPresent()) {
                log.info("Category with name '{}' already exists. Skipping.", categoryName);
                continue;
            }

            String categoryDescription = "This category is about " + categoryName.toLowerCase() + " and related topics";

            Category category = new Category();
            category.setName(categoryName);
            category.setDescription(categoryDescription);
            categoryRepository.save(category);

            log.info("Category created: {}", categoryName);
        }
    }
}
