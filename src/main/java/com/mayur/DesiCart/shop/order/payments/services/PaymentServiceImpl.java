package com.mayur.DesiCart.shop.order.payments.services;

import com.mayur.DesiCart.shop.cartAndCheckout.services.PaymentAdaptors.RazorPayServiceAdapter;
import com.mayur.DesiCart.shop.cartAndCheckout.services.PaymentAdaptors.razorpay.RazorPayConfig;
import com.mayur.DesiCart.shop.order.models.Order;
import com.mayur.DesiCart.shop.order.models.OrderStatus;
import com.mayur.DesiCart.shop.order.models.PaymentDetails;
import com.mayur.DesiCart.shop.order.orderRepository.OrderRepository;
import com.mayur.DesiCart.shop.order.orderRepository.PaymentDetailsRepository;
import com.mayur.DesiCart.shop.order.payments.dtos.PaymentVerifyRequest;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService{

    private final RazorPayServiceAdapter razorPayServiceAdapter;
    private final PaymentDetailsRepository paymentDetailsRepository;
    private final OrderRepository orderRepository;
    private final RazorPayConfig razorPayConfig;

    @Override
    public void verifyPayment(PaymentVerifyRequest req) {
        verifySignature(
                req.getRazorpayOrderId(),
                req.getRazorpayPaymentId(),
                req.getRazorpaySignature()
        );

        PaymentDetails paymentDetails = razorPayServiceAdapter.verifyPayment(req.getRazorpayOrderId());
        if (paymentDetails == null) {
            throw new IllegalStateException("Payment not completed on Razorpay");
        }
        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() ->
                        new IllegalStateException("Order not found: " + req.getOrderId())
                );

        order.setOrderStatus(OrderStatus.ORDER_PAID);
        orderRepository.save(order);
        paymentDetails.setStatus(OrderStatus.ORDER_PAID.name());
        paymentDetailsRepository.save(paymentDetails);
        log.info("Payment verified successfully for orderId={}", req.getOrderId());

    }

    private void verifySignature(String orderId, String paymentId, String signature){
        try {
            String payload = orderId + "|" + paymentId;
            String generatedSignature = Utils.getHash(payload, razorPayConfig.apiSecret);
            if (!generatedSignature.equals(signature)) {
                throw new SecurityException("Invalid Razorpay signature");
            }
        } catch (RazorpayException e) {
            log.error("Signature verification failed", e);
            throw new SecurityException("Payment verification failed");
        }
    }
}
