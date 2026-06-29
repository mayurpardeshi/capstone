package com.mayur.DesiCart.shop.order.payments.services;

import com.mayur.DesiCart.shop.order.payments.dtos.PaymentVerifyRequest;

public interface PaymentService {
    void verifyPayment(PaymentVerifyRequest request);
}
