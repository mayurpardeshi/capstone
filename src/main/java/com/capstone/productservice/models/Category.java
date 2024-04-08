package com.capstone.productservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Category extends BaseModal {
    private String title;

    public Category(String category) {
        this.title = category;
    }

    public Category() {

    }
}
