package com.mayur.DesiCart.shop.cartAndCheckout.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mayur.DesiCart.shop.cartAndCheckout.dtos.CartAddResponse;
import com.mayur.DesiCart.shop.cartAndCheckout.dtos.CartItemDto;
import com.mayur.DesiCart.shop.cartAndCheckout.models.Cart;
import com.mayur.DesiCart.shop.cartAndCheckout.models.CartItem;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(source = "id", target = "cartId")
    @Mapping(target = "items", expression = "java(mapActiveItems(cart.getCartItems()))")
    @Mapping(target = "totalItems", expression = "java(countActiveItems(cart.getCartItems()))")
    CartAddResponse toDto(Cart cart);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "totalPrice", target = "subTotal")
    CartItemDto toItemDto(CartItem item);

    default List<CartItemDto> mapActiveItems(Set<CartItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .filter(item -> !item.isDeleted())
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }

    // Custom logic to count only non-deleted items
    default int countActiveItems(Set<CartItem> items) {
        if (items == null) return 0;
        return (int) items.stream()
                .filter(item -> !item.isDeleted())
                .count();
    }
}
