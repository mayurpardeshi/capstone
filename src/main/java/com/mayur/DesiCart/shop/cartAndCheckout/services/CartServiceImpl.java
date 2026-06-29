package com.mayur.DesiCart.shop.cartAndCheckout.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mayur.DesiCart.shop.cartAndCheckout.models.Cart;
import com.mayur.DesiCart.shop.cartAndCheckout.repositories.CartItemRepository;
import com.mayur.DesiCart.shop.cartAndCheckout.repositories.CartRepository;
import com.mayur.DesiCart.shop.product.exception.ResourceNotFoundException;
import com.mayur.DesiCart.shop.user.models.User;
import com.mayur.DesiCart.shop.user.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
@Slf4j
@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final AtomicLong cartIdGenerator = new AtomicLong(0);
    @Override
    public Cart getCart() {
        // 1. Get Authentication from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName();

        // 2. Find user in DB
        User user = userRepository.findByUserId(userid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Return or Create Cart
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setTotalAmount(BigDecimal.ZERO);
                    return cartRepository.save(newCart);
                });
    }

    private final User getUserFromContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName();

        // 2. Find user in DB
        User user = userRepository.findByUserId(userid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user;

    }

    @Override
    public void clearCart() {
        User user = getUserFromContext();
        // Instead of orElseThrow, use ifPresent or a soft check
        cartRepository.findByUserId(user.getId()).ifPresent(cart -> {
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();
            cart.setTotalAmount(BigDecimal.ZERO);
            cartRepository.save(cart);
        });
        // If not present, we just log it and move on
        log.info("Cart already empty or not found for user: {}, skipping clearCart", user.getId());
    }

    public void clearCart(Long userId) {
        // Instead of orElseThrow, use ifPresent or a soft check
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();
            cart.setTotalAmount(BigDecimal.ZERO);
            cartRepository.save(cart);
        });
        // If not present, we just log it and move on
        log.info("Cart already empty or not found for user: {}, skipping clearCart", userId);
    }

    @Override
    public BigDecimal getTotalPrice() {
        Cart cart = getCart();
        return cart.getTotalAmount();
    }

    @Override
    public Cart initializeNewCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setTotalAmount(BigDecimal.ZERO);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public Optional<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }
}
