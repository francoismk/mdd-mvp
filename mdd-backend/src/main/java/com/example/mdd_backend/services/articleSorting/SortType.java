package com.example.mdd_backend.services.articleSorting;

public enum SortType {
    DATE_ASC,
    DATE_DESC;

    public static SortType fromString(String key) {
        try{
            return SortType.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sort type: " + key);
        }
    }
}
