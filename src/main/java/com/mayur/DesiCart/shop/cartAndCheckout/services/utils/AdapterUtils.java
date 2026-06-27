package com.mayur.DesiCart.shop.cartAndCheckout.services.utils;

import com.mayur.DesiCart.shop.cartAndCheckout.models.PaymentStatus;
import com.mayur.DesiCart.shop.order.models.Order;
import com.mayur.DesiCart.shop.order.models.OrderStatus;
import com.mayur.DesiCart.shop.order.models.PaymentDetails;
import com.mayur.DesiCart.shop.order.orderRepository.PaymentDetailsRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdapterUtils {

    private final PaymentDetailsRepository paymentDetailsRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;


    public void validateOrder(Order order){
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order amount must be greater than zero");
        }
        if (order.isDeleted()){
            throw new IllegalStateException("Order seems to be deleted and can't proceed");
        }

        if (order.getOrderStatus() != OrderStatus.ORDER_PAYMENT_INITIATED){
            throw new IllegalStateException("To place an Order order state must be ORDER_PAYMENT_INITIATED, but current Order state is: "+order.getOrderStatus());
        }
    }

    public boolean isPaymentAlreadyInitialized(Order order){
        return order.getPaymentDetails() != null && order.getPaymentDetails().getGatewayOrderId() != null;
    }

    public RazorpayClient createRazorpayClient() throws RazorpayException {
        return new RazorpayClient(keyId, keySecret);
    }

    public long convertToPaise(BigDecimal amount){
        return amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    public JSONObject buildRazorpayOrderRequest(Order order, long amountInPaise){
        JSONObject request = new JSONObject();
        request.put("amount", amountInPaise);
        request.put("currency", "INR");
        request.put("receipt", "txn_" + order.getId());
        return request;
    }

    public void savePaymentDetails(Order order, String gatewayOrderId){
        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setOrder(order);
        paymentDetails.setPaymentDate(LocalDateTime.now());
        paymentDetails.setAmount(order.getTotalAmount());
        paymentDetails.setStatus(PaymentStatus.CREATED.name());
        try {
            paymentDetailsRepository.save(paymentDetails);
        } catch (Exception e) {
            log.error("Something wrong while saving the payment detail : {}", paymentDetails);
            throw new RuntimeException(e);
        }
    }


    public void validateOrderAndPayment(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        if (order.getPaymentDetails() == null
                || order.getPaymentDetails().getGatewayOrderId() == null) {
            throw new IllegalStateException(
                    "Existing payment not found for orderId=" + order.getId());
        }
    }

    public BigDecimal resolvePaymentAmount(Order order, PaymentDetails paymentDetails) {

        if (paymentDetails.getAmount() != null) {
            return paymentDetails.getAmount();
        }

        if (order.getTotalAmount() != null) {
            return order.getTotalAmount();
        }

        throw new IllegalStateException(
                "Unable to resolve payment amount for orderId=" + order.getId());
    }

}
