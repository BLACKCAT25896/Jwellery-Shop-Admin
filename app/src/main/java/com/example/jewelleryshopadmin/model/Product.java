package com.example.jewelleryshopadmin.model;

public class Product {
    private String categoryId;
    private String productId;
    private String productName;
    private String productRegularPrice;
    private String productDiscountPrice;
    private String productImage;
    private int productDiscount;



    public Product() {
    }

    public Product(String categoryId, String productId, String productName, String productRegularPrice, String productImage, int productDiscount) {
        this.categoryId = categoryId;
        this.productId = productId;
        this.productName = productName;
        this.productRegularPrice = productRegularPrice;
        this.productImage = productImage;
        this.productDiscount = productDiscount;
    }

    public Product(String categoryId, String productId, String productName, String productRegularPrice, String productDiscountPrice, String productImage, int productDiscount) {
        this.categoryId = categoryId;
        this.productId = productId;
        this.productName = productName;
        this.productRegularPrice = productRegularPrice;
        this.productDiscountPrice = productDiscountPrice;
        this.productImage = productImage;
        this.productDiscount = productDiscount;
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

    public String getProductRegularPrice() {
        return productRegularPrice;
    }

    public String getProductDiscountPrice() {
        return productDiscountPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public int getProductDiscount() {
        return productDiscount;
    }
}
