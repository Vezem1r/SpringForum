package com.SuperForum.forum_app.dtos;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CategoryDto {

    private Long categoryId;

    private String name;

    private String description;
}
