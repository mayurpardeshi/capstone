package com.mayur.DesiCart.shop.order.mapper;

import org.mapstruct.Mapper;

import com.mayur.DesiCart.shop.order.dto.OrderItemDto;
import com.mayur.DesiCart.shop.order.models.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem orderItem);
    OrderItem toEntity(OrderItemDto orderItemDto);
}
