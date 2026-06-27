package com.mayur.DesiCart.shop.order.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.mayur.DesiCart.shop.common.models.BaseModel;

@Entity
@Getter
@Setter
public class PaymentDetails extends BaseModel {
    private String gatewayOrderId;    // order_Rz8YBUXdEPfw3c
    private String gatewayPaymentId;  // pay_Rz8ZLLa3vGiDVa
    private String gatewayName;       // RAZORPAY
    private String status;            // captured
    private LocalDateTime paymentDate;

    private String email;             // abc@example.com
    private String contact;           // +918885557660
    private String method;            // wallet
    private String wallet;            // jiomoney
    private String bank;              // (null in this case)
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;              // 847907 (in paise)
    private Integer tax;              // 3052
    private Integer fee;              // 20011        // PENDING, CAPTURED, FAILED

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
