package com.mayur.DesiCart.shop.order.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mayur.DesiCart.shop.order.kafka.producer.OrderEventProducer;
import com.mayur.DesiCart.shop.order.models.Order;
import com.mayur.DesiCart.shop.order.models.OrderStatus;
import com.mayur.DesiCart.shop.order.orderRepository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCleanupService {
    private static final Logger log = LoggerFactory.getLogger(OrderCleanupService.class);
    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private static final int ORDER_EXPIRY_CUTOFF_MIN = 10;
    private static final String ORDER_EXPIRY_CUTOFF_REASON = "Order Expired because it failed to reach Checkout within 10 min";
    private static final String PAYMENT_EXPIRY_CUTOFF_REASON = "Order-Payment Expired because it failed to reach PAID status within 10 min";
    // aggressive - runs every 15 min
    @Scheduled(fixedRate = 60000*15)
    @Transactional
    public void cancelExpiredOrder(){
        log.info("Starting background order cleanup task...");
        // 2. Handle ORDER_PLACED (10 min timeout)
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(10);
        int placedCount = expireOrdersByStatus(OrderStatus.ORDER_PLACED, ORDER_EXPIRY_CUTOFF_MIN, ORDER_EXPIRY_CUTOFF_REASON);
        List<Order> expiredOrders = orderRepository.findExpiredOrdersWithItems(
                OrderStatus.ORDER_PLACED, cutoff);
        for (Order order: expiredOrders){
            order.setOrderStatus(OrderStatus.ORDER_EXPIRED);
            orderRepository.save(order);

        }
        // 2. Handle ORDER_PAYMENT_INITIATED (10 min timeout)
        LocalDateTime paymentCutoff = LocalDateTime.now().minusMinutes(10);
        int paymentCount = expireOrdersByStatus(OrderStatus.ORDER_PAYMENT_INITIATED, ORDER_EXPIRY_CUTOFF_MIN, PAYMENT_EXPIRY_CUTOFF_REASON);
        if (placedCount > 0 || paymentCount > 0) {
            log.info("Cleanup complete: Expired {} placed orders and {} initiated payments.",
                    placedCount, paymentCount);
        }
        log.info("Moved all passive orders from last 1 min as ORDER_CANCELLED");
    }

    private int expireOrdersByStatus(OrderStatus status, int minutes, String reason) {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(minutes);
        List<Order> expired = orderRepository.findExpiredOrdersWithItems(status, cutoff);
        if (!expired.isEmpty()) {
            for (Order order: expired){
                order.setOrderStatus(OrderStatus.ORDER_EXPIRED);
                orderRepository.save(order);
                orderEventProducer.sendOrderCancelledEvent(order, reason);
            }
        }
        return expired.size();
    }
}
