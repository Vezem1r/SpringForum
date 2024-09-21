package com.SuperForum.forum_app.services.admin;

import com.SuperForum.forum_app.dtos.CategoryDto;

public interface AdminService {
    CategoryDto postCategory(CategoryDto categoryDto);
}
