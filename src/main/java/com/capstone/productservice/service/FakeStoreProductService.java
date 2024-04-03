package com.capstone.productservice.service;

import com.capstone.productservice.dtos.FakeStoreProductDto;
import com.capstone.productservice.dtos.FakeStoreProductListDto;
import com.capstone.productservice.dtos.ProductDto;
import com.capstone.productservice.models.Product;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
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
        FakeStoreProductDto object =
                restTemplate.getForObject("https://fakestoreapi.com/products/" + id, FakeStoreProductDto.class);
        //convert FakeStoreDtoObject to Product Object
        if(object == null){
            return null;
        }
        return convertFakeStoreProductDtoToProductObject(object);
    }

    @Override
    public List<Product> getAllProducts() {
        ResponseEntity<FakeStoreProductDto[]> response =
                restTemplate.getForEntity("https://fakestoreapi.com/products", FakeStoreProductDto[].class);
        FakeStoreProductDto[] fakeStoreProductDtos = response.getBody();
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < fakeStoreProductDtos.length; i++) {
            productList.add(convertFakeStoreProductDtoToProductObject(fakeStoreProductDtos[i]));
        }
        return productList;
    }

    @Override
    public List<Product> getAllProductList() {
        FakeStoreProductListDto response =
                restTemplate.getForObject("https://fakestoreapi.com/products", FakeStoreProductListDto.class);
        List<FakeStoreProductDto> fakeStoreProductListDto = response.getProducts();
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < fakeStoreProductListDto.size(); i++) {
            productList.add(convertFakeStoreProductDtoToProductObject(fakeStoreProductListDto.get(i)));
        }
        return productList;
    }

    @Override
    public Product updateProduct(Long id, ProductDto productDto) {
        //Product product = ProductDto.getProduct(productDto);
        RequestCallback requestCallback = restTemplate.httpEntityCallback(productDto,FakeStoreProductDto.class);
        HttpMessageConverterExtractor<FakeStoreProductDto> responseExtractor = new HttpMessageConverterExtractor<>(FakeStoreProductDto.class,restTemplate.getMessageConverters());
        FakeStoreProductDto fakeStoreProductDto = restTemplate.execute("https://fakestoreapi.com/products/"+id, HttpMethod.PUT,requestCallback,responseExtractor);

        return convertFakeStoreProductDtoToProductObject(fakeStoreProductDto);
    }

    @Override
    public Product createProduct(Product product) {
        return null;
    }

    @Override
    public Product deleteProduct(Long id) {
        //restTemplate.delete("https://fakestoreapi.com/products/"+id);
        RequestCallback requestCallback = restTemplate.httpEntityCallback(null,FakeStoreProductDto.class);
        HttpMessageConverterExtractor<FakeStoreProductDto> responseExtractor = new HttpMessageConverterExtractor<>(FakeStoreProductDto.class,restTemplate.getMessageConverters());
        FakeStoreProductDto fakeStoreProductDto = restTemplate.execute("https://fakestoreapi.com/products/"+id, HttpMethod.DELETE,requestCallback,responseExtractor);
        return convertFakeStoreProductDtoToProductObject(fakeStoreProductDto);
    }

    @Override
    public Product paritiallyUpdateProduct(Long id, ProductDto productDto) {
        System.out.println(productDto);
        RequestCallback requestCallback = restTemplate.httpEntityCallback(productDto,FakeStoreProductDto.class);
        HttpMessageConverterExtractor<FakeStoreProductDto> responseExtractor = new HttpMessageConverterExtractor<>(FakeStoreProductDto.class,restTemplate.getMessageConverters());
        FakeStoreProductDto fakeStoreProductDto = restTemplate.execute("https://fakestoreapi.com/products/"+id, HttpMethod.PATCH,requestCallback,responseExtractor);
        return convertFakeStoreProductDtoToProductObject(fakeStoreProductDto);
    }
}
