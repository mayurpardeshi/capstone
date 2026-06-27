package com.mayur.DesiCart.shop.order.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentWebhookDto;

import org.springframework.stereotype.Component;

@Component
public class RazorpayWebhookParser {
    public static PaymentWebhookDto parse(JsonNode root) {

        JsonNode paymentEntity = root
                .path("payload")
                .path("payment")
                .path("entity");

        if (paymentEntity.isMissingNode()) {
            throw new IllegalArgumentException("Invalid Razorpay webhook payload");
        }

        return PaymentWebhookDto.builder()
                .gatewayOrderId(paymentEntity.path("order_id").asText())
                .gatewayPaymentId(paymentEntity.path("id").asText())
                .amount(paymentEntity.path("amount").asLong())
                .method(paymentEntity.path("method").asText())
                .wallet(paymentEntity.path("wallet").asText(null))
                .bank(paymentEntity.path("bank").asText(null))
                .email(paymentEntity.path("email").asText(null))
                .contact(paymentEntity.path("contact").asText(null))
                .fee(paymentEntity.path("fee").asInt())
                .tax(paymentEntity.path("tax").asInt())
                .build();
    }

}
