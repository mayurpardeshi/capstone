package com.capstone.productservice.controller;

import com.capstone.productservice.dtos.ProductDto;
import com.capstone.productservice.models.Product;
import com.capstone.productservice.service.IProductService;
import com.capstone.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}/exception")
    public Product getProductByIdException(@PathVariable(name = "id") Long id) throws ArithmeticException{
        //return productService.getProductById(id);
        //throw new ArithmeticException("heelo");
        long a = 10l/id;
        return null;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/list")
    public List<Product> getAllProductList() {
        return productService.getAllProductList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long id,@RequestBody ProductDto product){
        Product updatedProduct = productService.updateProduct(id, product);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}
