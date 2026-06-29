package com.mayur.DesiCart.shop.cartAndCheckout.services;

import java.math.BigDecimal;
import java.util.Optional;

import com.mayur.DesiCart.shop.cartAndCheckout.models.Cart;
import com.mayur.DesiCart.shop.user.models.User;

public interface CartService {
    Cart getCart();
    void clearCart();
    void clearCart(Long id);
    BigDecimal getTotalPrice();
    Cart initializeNewCart(User user);
    Optional<Cart> getCartByUserId(Long userId);
}
