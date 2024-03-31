package com.capstone.productservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {
    private  Long id;
    private String title;

    public Category(String category) {
        this.title = category;
    }
}
