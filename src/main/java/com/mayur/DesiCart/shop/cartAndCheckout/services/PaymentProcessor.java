package com.mayur.DesiCart.shop.cartAndCheckout.services;

import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentInitializationResponse;
import com.mayur.DesiCart.shop.order.models.Order;
import com.mayur.DesiCart.shop.order.models.PaymentDetails;

public interface PaymentProcessor {
    PaymentInitializationResponse initiatePayment(Order order);
    String setUpGateway();
    PaymentDetails verifyPayment(String gatewayOrderId);
}

