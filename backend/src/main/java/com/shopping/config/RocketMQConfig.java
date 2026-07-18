package com.shopping.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ 配置
 */
@Configuration
@ConditionalOnProperty(name = "rocketmq.enabled", havingValue = "true", matchIfMissing = false)
public class RocketMQConfig {

    public static final String ORDER_TOPIC = "order-events";
    public static final String BEHAVIOR_TOPIC = "user-behavior";
    public static final String ORDER_GROUP = "order-consumer-group";

    // 注：使用 RocketMQ 需要引入 rocketmq-spring-boot-starter
    // 当前保留配置骨架，实际启用时取消注释 pom.xml 中 rocketmq 依赖
}
