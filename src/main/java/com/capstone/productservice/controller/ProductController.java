package com.capstone.productservice.controller;

import com.capstone.productservice.dtos.ProductDto;
import com.capstone.productservice.models.Product;
import com.capstone.productservice.service.IProductService;
import com.capstone.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private IProductService productService;
    ProductController(@Qualifier("SelfProductService") IProductService productService){
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
        //long a = 10l/id; //throwing exception if id == 0
        return productService.getProductById(id);
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

    @PatchMapping("/{id}")
    public ResponseEntity<Product> updatePartiallyProduct(@PathVariable("id") Long id,@RequestBody ProductDto productDto){
        Product updatedProduct = productService.paritiallyUpdateProduct(id,productDto);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") Long id){
        Product deletedProduct = productService.deleteProduct(id);
        return new ResponseEntity<>(deletedProduct,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product){
        Product product1 = productService.createProduct(product);
        return new ResponseEntity<>(product1,HttpStatus.OK);
    }
}
