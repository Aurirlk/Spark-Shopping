package com.shopping.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean public NewTopic orderEventTopic() { return TopicBuilder.name("order-events").partitions(3).replicas(1).build(); }
    @Bean public NewTopic userBehaviorTopic() { return TopicBuilder.name("user-behavior").partitions(6).replicas(1).build(); }
}
