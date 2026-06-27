package com.mayur.DesiCart.shop.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.mayur.DesiCart.shop.notifications.events.PaymentNotificationEvent;

@Service
@Slf4j
public class NotificationConsumer {
    @KafkaListener(topics = "payment-notifications", groupId = "notification-group")
    public void consumePaymentNotification(PaymentNotificationEvent event){
        log.info("Received notification event from Kafka: {}", event);
        // Logic to send Email or SMS
        log.info("Notification successfully processed for Order: {}", event.orderId());

    }
}
