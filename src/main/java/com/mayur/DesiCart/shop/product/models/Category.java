package com.mayur.DesiCart.shop.product.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mayur.DesiCart.shop.common.models.BaseModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "category")
public class Category extends BaseModel {
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true, length = 1000)
    private String description;

    public Category(String name){
        this.name = name;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
