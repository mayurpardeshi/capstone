package com.capstone.productservice.controller;

import com.capstone.productservice.models.Product;
import com.capstone.productservice.service.IProductService;
import com.capstone.productservice.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private IProductService productService;
    ProductController(IProductService productService){
        this.productService = productService;
    }
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable(name = "id") Long id){
        return productService.getProductById(id);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
}
