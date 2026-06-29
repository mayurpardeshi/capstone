package com.mayur.DesiCart.shop.cartAndCheckout.dtos;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderId;
    private String customerName;
    private String phone;
    private Integer amount;
}
