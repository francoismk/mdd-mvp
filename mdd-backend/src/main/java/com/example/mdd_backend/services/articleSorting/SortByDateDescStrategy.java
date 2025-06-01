package com.example.mdd_backend.services.articleSorting;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class SortByDateDescStrategy implements ArticleSortStrategy {

    @Override
    public boolean supports(SortType type) {
        return type == SortType.DATE_DESC;
    }

    @Override
    public Sort getSort() {
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}
