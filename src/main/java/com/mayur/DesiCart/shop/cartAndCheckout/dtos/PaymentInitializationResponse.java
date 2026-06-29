package com.mayur.DesiCart.shop.cartAndCheckout.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentInitializationResponse {
    private String gatewayName; // RAZORPAY, PHONEPE
    private String orderId;     // Gateway's internal Order ID
    private String paymentUrl;  // For PhonePe redirect
    private Long amount;        // In paise
    private String currency;
    private String apiKey;      // Public key for frontend SDK
    private String customerName;
    private String customerEmail;
}
