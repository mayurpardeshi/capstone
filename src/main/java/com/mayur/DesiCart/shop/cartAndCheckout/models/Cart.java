package com.mayur.DesiCart.shop.cartAndCheckout.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mayur.DesiCart.shop.common.models.BaseModel;
import com.mayur.DesiCart.shop.user.models.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@NoArgsConstructor
public class Cart extends BaseModel {
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @JsonBackReference // used to break the infinite recursion problem
    @JoinColumn(name = "user_id")
    @OneToOne
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    public void addItem(CartItem cartItem){
        cartItems.add(cartItem);
        cartItem.setCart(this);
        updateTotalAmount();

    }

    // now relying on soft delete
    public void removeItem(CartItem cartItem){
        cartItems.remove(cartItem);
        cartItem.setCart(null);
        updateTotalAmount();
    }

    public void
    updateTotalAmount(){
        this.totalAmount = cartItems.stream()
                .filter(cartItem -> !cartItem.isDeleted())
                .map(this::calculateSubtotal) // Cleaner mapping
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateSubtotal(CartItem item) {
        if (item.getUnitPrice() == null) {
            return BigDecimal.ZERO;
        }
        return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}
