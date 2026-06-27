package com.mayur.DesiCart.shop.order.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.mayur.DesiCart.shop.order.kafka.events.OrderCancelledEvent;
import com.mayur.DesiCart.shop.order.models.Order;
import com.mayur.DesiCart.shop.order.models.OrderItem;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-cancelled-topic";

    public void sendOrderCancelledEvent(Order order, String reason){
        Map<Long, Integer> items = order.getOrderItems().stream()
                .filter(orderItem -> orderItem.getProduct() != null)
                .collect(Collectors.toMap(
                        orderItem -> orderItem.getOrder().getId(),
                        OrderItem::getQuantity,
                        Integer::sum
                ));

        OrderCancelledEvent event = new OrderCancelledEvent(order.getId(), reason, LocalDateTime.now(), items);
        kafkaTemplate.send(TOPIC, String.valueOf(order.getId()), event);
    }
}
