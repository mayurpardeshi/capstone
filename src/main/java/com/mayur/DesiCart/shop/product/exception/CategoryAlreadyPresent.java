package com.mayur.DesiCart.shop.product.exception;

public class CategoryAlreadyPresent extends RuntimeException{
    public CategoryAlreadyPresent(String message){
        super(message);
    }
}
