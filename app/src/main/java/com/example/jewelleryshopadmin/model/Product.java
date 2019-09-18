package com.example.jewelleryshopadmin.model;

public class Product {
    private String productId;
    private String productName;
    private String productPrice;
    private String productImage;

    public Product() {
    }

    public Product(String productId, String productName, String productPrice, String productImage) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImage = productImage;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductImage() {
        return productImage;
    }
}
