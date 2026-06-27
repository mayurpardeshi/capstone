package com.mayur.DesiCart.shop.order.payments.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyRequest {
    private Long orderId;                  // DB order id
    private String razorpayOrderId;         // order_xxx
    private String razorpayPaymentId;       // pay_xxx
    private String razorpaySignature;       // signature
}
