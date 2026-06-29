package com.mayur.DesiCart.shop.cartAndCheckout.services;

import com.mayur.DesiCart.shop.cartAndCheckout.models.Cart;
import com.mayur.DesiCart.shop.cartAndCheckout.models.CartItem;
import com.mayur.DesiCart.shop.product.exception.ProductNotFoundException;

public interface CartItemService {
    Cart addCartItem(Long cartId, Long productId, int quantity) throws ProductNotFoundException;
    Cart removeCartItem(Long productId);
    Cart updateItemQuantity(Long productId, int quantity);
    CartItem getCartItem(Long productId);

}
