package com.mayur.DesiCart.shop.order.exceptions;

public class ProductOutOfStockException extends RuntimeException{
    public ProductOutOfStockException(String message){
        super(message);
    }
}
