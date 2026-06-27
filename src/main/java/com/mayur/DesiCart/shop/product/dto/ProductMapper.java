package com.mayur.DesiCart.shop.product.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mayur.DesiCart.shop.product.models.Product;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.name", target = "categoryName")
    ProductDto productToDto(Product product);
    Product dtoToProduct(ProductDto productDto);
    List<ProductDto> productsToDtos(List<Product> products);
}
