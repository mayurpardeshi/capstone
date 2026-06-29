package com.mayur.DesiCart.shop.order.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import com.mayur.DesiCart.shop.common.models.BaseModel;
import com.mayur.DesiCart.shop.product.models.Product;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem extends BaseModel {
    private int quantity;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private String name;
    private String brand;
    private String description;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public OrderItem(Order order, Product product, int quantity, BigDecimal price){
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }
}
