package com.mayur.DesiCart.shop.notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.mayur.DesiCart.shop.notifications.events.PaymentNotificationEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {
    private final KafkaTemplate<String, PaymentNotificationEvent> kafkaTemplate;
    private static final String TOPIC = "payment-notifications";

    public void sendNotification(PaymentNotificationEvent event){
        log.info("Sending payment notification event to Kafka for Order: {}", event.orderId());
        kafkaTemplate.send(TOPIC, String.valueOf(event.orderId()), event);
    }
}
