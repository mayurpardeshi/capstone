package com.mayur.DesiCart.shop.cartAndCheckout.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CartAddResponse {
    private Long cartId;
    private String message;
    private int totalItems;
    private BigDecimal totalAmount;
    private List<CartItemDto> items;
}
