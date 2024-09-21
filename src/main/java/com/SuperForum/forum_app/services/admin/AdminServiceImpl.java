package com.SuperForum.forum_app.services.admin;

import com.SuperForum.forum_app.dtos.CategoryDto;
import com.SuperForum.forum_app.entities.Category;
import com.SuperForum.forum_app.entities.User;
import com.SuperForum.forum_app.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto postCategory(CategoryDto categoryDto) {

        Optional<Category> existingCategoryByName = categoryRepository.findByName(categoryDto.getName());

        if (existingCategoryByName.isPresent()) {
            return null;
        }

        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        Category createdCategory = categoryRepository.save(category);
        CategoryDto createdCategoryDto = new CategoryDto();
        //createdCategoryDto.setCategoryId(createdCategory.getCategoryId());
        createdCategoryDto.setName(createdCategory.getName());
        createdCategoryDto.setDescription(createdCategory.getDescription());
        return createdCategoryDto;
    }
}
