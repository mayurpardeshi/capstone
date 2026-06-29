package com.mayur.DesiCart.shop.cartAndCheckout.dtos;

import lombok.Data;

import java.math.BigDecimal;

import com.mayur.DesiCart.shop.product.dto.ProductDto;

@Data
public class CartItemRequestDto {
    private Long itemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private ProductDto product;
}
