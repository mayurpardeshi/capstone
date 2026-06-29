package com.mayur.DesiCart.shop.order.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mayur.DesiCart.shop.common.models.BaseModel;
import com.mayur.DesiCart.shop.user.models.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.control.MappingControl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseModel {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "gateway_order_id", nullable = true)
    private String gatewayOrderId;

    @Column(name = "gateway_name")
    private String gatewayName; // Stores "RAZORPAY" or "PHONEPE"

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PaymentDetails paymentDetails;

}
