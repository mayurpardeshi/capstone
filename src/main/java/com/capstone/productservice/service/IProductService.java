package com.capstone.productservice.service;

import com.capstone.productservice.models.Product;

import java.util.List;

public interface IProductService {
    Product getProductById(Long id);
    List<Product> getAllProducts();

    Product updateProduct(Product product);

    Product createProduct(Product product);

    void deleteProduct();
}
