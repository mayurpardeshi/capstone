package com.mayur.DesiCart.shop.cartAndCheckout.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentInitializationResponse;
import com.mayur.DesiCart.shop.order.models.Order;
import com.mayur.DesiCart.shop.order.models.OrderStatus;
import com.mayur.DesiCart.shop.order.models.PaymentDetails;
import com.mayur.DesiCart.shop.order.orderRepository.PaymentDetailsRepository;
import com.mayur.DesiCart.shop.product.exception.ResourceNotFoundException;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class PaymentService {
    public final Map<String, PaymentProcessor> paymentProcessorMap;
    private final PaymentDetailsRepository paymentDetailsRepository;


    public PaymentInitializationResponse processPayment(Order order, String gateway){
        PaymentProcessor paymentProcessor = paymentProcessorMap.get(gateway);
        if (paymentProcessor == null){
            throw new ResourceNotFoundException("Payment gateway '" + gateway + "' is not supported.");
        }
        PaymentInitializationResponse response = paymentProcessor.initiatePayment(order);
        PaymentDetails payment = paymentDetailsRepository.findByOrderId(order.getId())
                .orElseGet(() -> {
                    PaymentDetails newPayment = new PaymentDetails();
                    newPayment.setOrder(order);
                    return newPayment;
                });


        payment.setGatewayName(gateway);
        payment.setGatewayOrderId(response.getOrderId());
        payment.setStatus(OrderStatus.ORDER_PAYMENT_INITIATED.name());
        order.setOrderStatus(OrderStatus.ORDER_PAYMENT_INITIATED);
        order.setGatewayOrderId(payment.getGatewayOrderId());
        payment.setOrder(order);
        order.setPaymentDetails(payment);
        paymentDetailsRepository.save(payment);

        return response;
    }
}
