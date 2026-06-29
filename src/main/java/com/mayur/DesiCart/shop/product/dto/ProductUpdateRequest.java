package com.mayur.DesiCart.shop.product.dto;

import lombok.Data;

import java.math.BigDecimal;

import com.mayur.DesiCart.shop.product.models.Category;
@Data
public class ProductUpdateRequest {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;
}
