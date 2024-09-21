package com.SuperForum.forum_app.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "name", length = 55, nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = 255)
    private String description;
}
