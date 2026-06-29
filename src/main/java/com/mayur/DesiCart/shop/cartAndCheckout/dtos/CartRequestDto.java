package com.mayur.DesiCart.shop.cartAndCheckout.dtos;

import java.math.BigDecimal;
import java.util.Set;

public class CartRequestDto {
    private Long cartId;
    private Set<CartItemRequestDto> items;
    private BigDecimal totalAmount;
}
