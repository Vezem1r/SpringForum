package com.back_end.forum.model;

import jakarta.persistence.*;
import lombok.Data;

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

    @Column(name = "description")
    private String description;
}
