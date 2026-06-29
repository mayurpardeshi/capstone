package com.mayur.DesiCart.shop.cartAndCheckout.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mayur.DesiCart.shop.cartAndCheckout.models.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void deleteAllByCartId(Long cartId);
}
