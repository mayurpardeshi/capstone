package com.capstone.productservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Category {
    @Id
    private  Long id;
    private String title;

    public Category(String category) {
        this.title = category;
    }
}
