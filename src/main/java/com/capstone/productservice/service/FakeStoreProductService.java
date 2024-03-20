package com.capstone.productservice.service;

import com.capstone.productservice.dtos.FakeStoreProductDto;
import com.capstone.productservice.models.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
@Service
public class FakeStoreProductService implements IProductService {
    private RestTemplate restTemplate;
    FakeStoreProductService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    private Product convertFakeStoreProductDtoToProductObject(FakeStoreProductDto fakeStoreProductDto){
        Product product = new Product();
        product.setId(fakeStoreProductDto.getId());
        product.setImage(fakeStoreProductDto.getImage());
        product.setCategory(null);
        product.setPrice(fakeStoreProductDto.getPrice());
        //product.setRating(fakeStoreProductDto.getRating());
        product.setTitle(fakeStoreProductDto.getTitle());
        product.setDescription(fakeStoreProductDto.getDescription());
        return product;
    }
    @Override
    public Product getProductById(Long id) {
        //call fake store API
        FakeStoreProductDto object = restTemplate.getForObject("https://fakestoreapi.com/products/" + id, FakeStoreProductDto.class);
        //convert FakeStoreDtoObject to Product Object
        if(object == null){
            return null;
        }
        return convertFakeStoreProductDtoToProductObject(object);
    }

    @Override
    public List<Product> getAllProducts() {
        ResponseEntity<FakeStoreProductDto[]> response = restTemplate.getForEntity("https://fakestoreapi.com/products", FakeStoreProductDto[].class);
        FakeStoreProductDto[] fakeStoreProductDtos = response.getBody();
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < fakeStoreProductDtos.length; i++) {
            productList.add(convertFakeStoreProductDtoToProductObject(fakeStoreProductDtos[i]));
        }
        return productList;
    }

    @Override
    public Product updateProduct(Product product) {
        return null;
    }

    @Override
    public Product createProduct(Product product) {
        return null;
    }

    @Override
    public void deleteProduct() {

    }
}
