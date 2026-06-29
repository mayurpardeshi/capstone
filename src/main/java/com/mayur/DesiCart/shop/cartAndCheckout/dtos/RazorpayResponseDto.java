package com.mayur.DesiCart.shop.cartAndCheckout.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RazorpayResponseDto {
    private String applicationKey; // rzp_test_... key
    private String amount;         // Amount in paise
    private String currency;       // "INR"
    private String orderId;        // order_Rz... ID from Razorpay
    private String name;           // "DesiCart"
    private String description;    // "Purchase for Order #8"
    private String customerEmail;
    private String customerContact;
}
