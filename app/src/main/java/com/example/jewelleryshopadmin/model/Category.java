package com.example.jewelleryshopadmin.model;

public class Category {
    private String userId;
    private String categoryId;
    private String categoryName;
    private String categoryImage;

    public Category() {
    }

    public Category(String userId, String categoryId, String categoryName, String categoryImage) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public String getUserId() {
        return userId;
    }
}
