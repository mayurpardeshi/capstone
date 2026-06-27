package com.mayur.DesiCart.shop.cartAndCheckout.services.PaymentAdaptors;

import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentInitializationResponse;
import com.mayur.DesiCart.shop.cartAndCheckout.models.PaymentType;
import com.mayur.DesiCart.shop.cartAndCheckout.services.PaymentProcessor;
import com.mayur.DesiCart.shop.order.models.Order;
import com.mayur.DesiCart.shop.order.models.PaymentDetails;

public class PhonePayServiceAdapter implements PaymentProcessor {


    @Override
    public PaymentInitializationResponse initiatePayment(Order order) {
        return null;
    }

    @Override
    public String setUpGateway() {
        return PaymentType.PHONE_PAY.toString();
    }

    @Override
    public PaymentDetails verifyPayment(String gatewayOrderId) {
        return null;
    }
}
