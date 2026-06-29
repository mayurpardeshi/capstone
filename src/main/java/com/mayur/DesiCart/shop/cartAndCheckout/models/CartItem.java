package com.mayur.DesiCart.shop.cartAndCheckout.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mayur.DesiCart.shop.common.models.BaseModel;
import com.mayur.DesiCart.shop.product.models.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE cart_item SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class CartItem extends BaseModel {
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public void setTotalPrice() {
        if (this.unitPrice != null && this.quantity > 0) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }
}
