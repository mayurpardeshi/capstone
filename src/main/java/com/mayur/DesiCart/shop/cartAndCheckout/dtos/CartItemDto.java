package com.mayur.DesiCart.shop.cartAndCheckout.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemDto {
    private Long productId;
    private String productName;
    private int qty;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
    private String productThumbnail;
}
