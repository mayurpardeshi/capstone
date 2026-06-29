package com.mayur.DesiCart.shop.product.exception;

public class ProductAlreadyPresentException extends RuntimeException {

    private final String name;
    private final String brand;

    public ProductAlreadyPresentException(String name, String brand) {
        super(String.format(
                "Product already exists with name='%s' and brand='%s'", name, brand
        ));
        this.name = name;
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }
}

