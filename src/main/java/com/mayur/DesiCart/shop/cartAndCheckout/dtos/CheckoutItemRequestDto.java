package com.mayur.DesiCart.shop.cartAndCheckout.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutItemRequestDto {
    private String productName;
    private int quantity;
    private double price;
    private Long productId;
    private Long userId;
    private String currency;
    private Long orderId;
}
