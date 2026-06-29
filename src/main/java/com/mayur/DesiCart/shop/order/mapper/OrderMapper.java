package com.mayur.DesiCart.shop.order.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mayur.DesiCart.shop.order.dto.OrderDto;
import com.mayur.DesiCart.shop.order.models.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "orderStatus", target = "status")
    OrderDto toDto(Order order);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "status", target = "orderStatus")
    Order toEntity(OrderDto orderDto);


}
