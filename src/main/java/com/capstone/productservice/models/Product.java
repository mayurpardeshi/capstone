package com.capstone.productservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private Long id;
    private String title;
    private double price;
    private String description;
    private Category category;
    private String image;
    //private Rating rating;
}
