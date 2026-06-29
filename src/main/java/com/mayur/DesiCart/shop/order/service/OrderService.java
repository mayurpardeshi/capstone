package com.mayur.DesiCart.shop.order.service;

import java.util.List;

import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentInitializationResponse;
import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentWebhookDto;
import com.mayur.DesiCart.shop.order.dto.OrderDto;
import com.mayur.DesiCart.shop.order.models.Order;

public interface OrderService {
    OrderDto placeOrder();
    OrderDto getOrder(Long orderId);

    List<OrderDto> getOrderByUserId(Long userId);

    void completeOrder(PaymentWebhookDto dto);

    OrderDto convertToDto(Order order);

    OrderDto checkout(Long orderId);
    PaymentInitializationResponse initiatePayment(Long orderId, String gateway);
    void handlePaymentSuccess(PaymentWebhookDto dto);
    void handlePaymentFailure(PaymentWebhookDto dto);
    void handlePaymentCancelled(PaymentWebhookDto dto);

}
