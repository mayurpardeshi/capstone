package com.mayur.DesiCart.shop.product.utils;

import org.springframework.stereotype.Component;

import com.mayur.DesiCart.shop.product.dto.AddProductRequest;

import java.math.BigDecimal;
@Component
public class ProductUtils {
    public boolean isBlank(String value){
        return value == null || value.trim().isEmpty();
    }

    public void validateAddProductRequest(AddProductRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("AddProductRequest cannot be null");
        }

        if (isBlank(request.getName())) {
            throw new IllegalArgumentException("Product name is required");
        }

        if (isBlank(request.getBrand())) {
            throw new IllegalArgumentException("Product brand is required");
        }

        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }

        if (request.getInventory() < 0) {
            throw new IllegalArgumentException("Inventory cannot be negative");
        }

        if (request.getCategory() == null || isBlank(request.getCategory().getName())) {
            throw new IllegalArgumentException("Category name is required");
        }
    }
}
