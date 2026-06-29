package com.mayur.DesiCart.shop.cartAndCheckout.services;

import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentInitializationResponse;

public interface CheckoutService {
    PaymentInitializationResponse checkout(Long orderId, String gateway);

    }

