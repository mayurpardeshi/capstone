package com.capstone.productservice.service;

import com.capstone.productservice.dtos.ProductDto;
import com.capstone.productservice.models.Category;
import com.capstone.productservice.models.Product;
import com.capstone.productservice.projections.ProductWithIdAndTitle;
import com.capstone.productservice.repositories.CategoryReporsitory;
import com.capstone.productservice.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("SelfProductService")
public class SelfProductService implements IProductService {
    ProductRepository productRepository;
    CategoryReporsitory categoryReporsitory;
    SelfProductService(ProductRepository productRepository,CategoryReporsitory categoryReporsitory){
        this.productRepository = productRepository;
        this.categoryReporsitory = categoryReporsitory;
    }
    @Override
    public Product getProductById(Long id) {
        //fetch Product with given id from DB
        Optional<Product> optProduct = productRepository.findById(id);
        if(!optProduct.isEmpty()){
            return optProduct.get();
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> list = productRepository.findAll();
        return list;
    }

    @Override
    public List<Product> getAllProductList() {
        List<Product> list = productRepository.findAll();
        return list;
    }

    @Override
    public Product updateProduct(Long id, ProductDto productDto) {
        if(productDto != null){

        }
        return null;
    }

    @Override
    public Product createProduct(Product product) {
        Date now = new Date();
        Category category = product.getCategory();
        if(category != null && category.getId() == null){
            category.setCreatedAt(now);
            category.setUpdatedAt(now);
            categoryReporsitory.save(category);
        }
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        return productRepository.save(product);
    }

    @Override
    public Product deleteProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        productRepository.deleteById(id);
        if(product.isPresent()){
            Product obj = product.get();
            obj.setUpdatedAt(new Date());
            return obj;
        }
        return null;
    }

    @Override
    public Product paritiallyUpdateProduct(Long id, ProductDto productDto) {
        return null;
    }

    @Override
    public ProductWithIdAndTitle getProdcutWithIdAndTitle(Long id){
        List<ProductWithIdAndTitle> obj = productRepository.someRandomQuery(id);
        return obj.get(0);
    }
}
