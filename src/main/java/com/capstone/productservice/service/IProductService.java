package com.capstone.productservice.service;

import com.capstone.productservice.dtos.ProductDto;
import com.capstone.productservice.models.Product;

import java.util.List;

public interface IProductService {
    Product getProductById(Long id);
    List<Product> getAllProducts();

    List<Product> getAllProductList();

    Product updateProduct(Long id, ProductDto productDto);

    Product createProduct(Product product);

    Product deleteProduct(Long id);

    Product paritiallyUpdateProduct(Long id, ProductDto productDto);
}
