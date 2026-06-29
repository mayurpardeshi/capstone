package com.mayur.DesiCart.shop.cartAndCheckout.dtos;

import com.mayur.DesiCart.shop.cartAndCheckout.models.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentWebhookDto {
    // Idempotency
    private String eventId;

    // IDs used for linking
    private String gatewayOrderId;
    private String gatewayPaymentId;

    // Status
    private PaymentStatus paymentStatus;

    // Customer Info
    private String email;
    private String contact;

    // Payment Metadata
    private String method;
    private String wallet;
    private String bank;

    // Financial Details (smallest unit)
    private Long amount;
    private Integer tax;
    private Integer fee;

    // Failure info (only for FAILED / CANCELLED)
    private String failureCode;
    private String failureReason;

    // Audit
    private Long gatewayEventTime;
}
