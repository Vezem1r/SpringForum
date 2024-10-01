package com.back_end.forum.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SearchRequest {
    private String title;
    private List<String> tags;
    private Long categoryId;
    private Integer minRating;
    private Integer maxRating;
    private LocalDateTime updatedAfter;
    private LocalDateTime updatedBefore;
    private String sortBy; // "rating", "createdAt", "updatedAt"
    private String sortOrder; // "asc", "desc"
}
