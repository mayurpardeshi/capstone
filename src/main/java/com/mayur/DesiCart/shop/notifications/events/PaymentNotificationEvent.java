package com.mayur.DesiCart.shop.notifications.events;

import java.math.BigDecimal;

public record PaymentNotificationEvent(
        Long orderId,
        String userEmail,
        String userName,
        BigDecimal amount,
        String status
) {
}
