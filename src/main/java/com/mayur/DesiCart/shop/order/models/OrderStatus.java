package com.mayur.DesiCart.shop.order.models;

public enum OrderStatus {
    ORDER_PLACED,
    ORDER_PENDING,            // Order created, items moved from cart
    ORDER_CHECKED_OUT,
    ORDER_EXPIRED,
    ORDER_PAYMENT_INITIATED,  // User clicked "Pay", Razorpay ID generated
    ORDER_PAID,               // Webhook confirmed success
    ORDER_PAYMENT_FAILED,     // Webhook confirmed failure or user cancelled
    ORDER_PAYMENT_CANCELLED,
    ORDER_SHIPPED,
    ORDER_DELIVERED,

}
