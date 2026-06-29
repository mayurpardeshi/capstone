package com.mayur.DesiCart.shop.common.configurations;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {
    public NewTopic notificationTopic(){
        return TopicBuilder.name("payment-notifications")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
