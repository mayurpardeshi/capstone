package com.capstone.productservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product extends BaseModal {
    private String title;
    private double price;
    private String description;
    @ManyToOne
    private Category category;
    private String image;
    //private Rating rating;
}
