package com.capstone.productservice.dtos;

import com.capstone.productservice.models.Category;
import com.capstone.productservice.models.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private Long id;
    private String title;
    private double price;
    private String description;
    private String category;
    private String image;

    public static Product getProduct(ProductDto productDto) {
        Product product = new Product();
        product.setDescription(productDto.getDescription());
        product.setTitle(productDto.getTitle());
        product.setId(productDto.getId());
        product.setImage(productDto.getImage());
        product.setPrice(productDto.getPrice());
        product.setCategory(new Category(productDto.getCategory()));
        return product;
    }
}
