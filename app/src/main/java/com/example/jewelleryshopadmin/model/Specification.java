package com.example.jewelleryshopadmin.model;

public class Specification {
    private String userId;
    private String categoryId;
    private String productId;
    private String brandName;
    private String boxContent;
    private String sku;

    public Specification() {
    }

    public Specification(String userId, String categoryId, String productId, String brandName, String boxContent, String sku) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.productId = productId;
        this.brandName = brandName;
        this.boxContent = boxContent;
        this.sku = sku;
    }

    public String getUserId() {
        return userId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getProductId() {
        return productId;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getBoxContent() {
        return boxContent;
    }

    public String getSku() {
        return sku;
    }
}
