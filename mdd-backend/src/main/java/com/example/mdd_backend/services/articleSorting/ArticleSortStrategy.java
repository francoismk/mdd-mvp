package com.example.mdd_backend.services.articleSorting;

import org.springframework.data.domain.Sort;

public interface ArticleSortStrategy {
    boolean supports(SortType type);
    Sort getSort();
}
