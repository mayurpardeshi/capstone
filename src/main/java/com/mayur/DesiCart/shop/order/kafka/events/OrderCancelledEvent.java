package com.mayur.DesiCart.shop.order.kafka.events;

import java.time.LocalDateTime;
import java.util.Map;

public record OrderCancelledEvent(
        Long orderId,
        String reason,
        LocalDateTime timestamp,
        Map<Long, Integer> expiredItemsToQuantity
) {}
