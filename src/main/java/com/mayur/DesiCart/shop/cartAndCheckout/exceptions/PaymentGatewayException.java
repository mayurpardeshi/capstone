package com.mayur.DesiCart.shop.cartAndCheckout.exceptions;

public class PaymentGatewayException extends RuntimeException{
    public PaymentGatewayException(String message){
        super(message);
    }
}
