package com.mayur.DesiCart.shop.product.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mayur.DesiCart.shop.common.models.BaseModel;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
@Table(name = "product")
public class Product extends BaseModel {
    @Column(nullable = false)
    private String name;

    // we never sell non-branded products
    @Column(nullable = false)
    private String brand;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // if inventory is null, it means its arriving soon
    @Column(nullable = true)
    private Integer inventory;

    // bounding description size
    @Column(nullable = true, length = 1000)
    private String description;

    // there can be a product without a category, LAZY prevents N+1 hell
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Image> images;
}
