package com.example.jewelleryshopadmin.model;

public class Product {
    private String userId;
    private String categoryId;
    private String productId;
    private String productName;
    private double productRegularPrice;
    private double productDiscountPrice;
    private String productImage;
    private int productDiscount;



    public Product() {
    }

    public Product(String userId, String categoryId, String productId, String productName, double productRegularPrice, double productDiscountPrice, String productImage, int productDiscount) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.productId = productId;
        this.productName = productName;
        this.productRegularPrice = productRegularPrice;
        this.productDiscountPrice = productDiscountPrice;
        this.productImage = productImage;
        this.productDiscount = productDiscount;
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

    public String getProductName() {
        return productName;
    }

    public double getProductRegularPrice() {
        return productRegularPrice;
    }

    public double getProductDiscountPrice() {
        return productDiscountPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public int getProductDiscount() {
        return productDiscount;
    }
}
