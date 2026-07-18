package com.shopping.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "shopping-order-group")
    public void handleOrderEvent(ConsumerRecord<String, String> record) {
        try {
            OrderEvent event = objectMapper.readValue(record.value(), OrderEvent.class);
            log.info("收到订单事件: type={}, orderNo={}", event.getEventType(), event.getOrderNo());
            switch (event.getEventType()) {
                case "ORDER_CREATED" -> log.info("处理订单创建: orderNo={}", event.getOrderNo());
                case "ORDER_PAID"    -> log.info("处理订单支付: orderNo={}", event.getOrderNo());
                case "ORDER_CANCELLED" -> log.info("处理订单取消: orderNo={}", event.getOrderNo());
                default -> log.warn("未知订单事件: {}", event.getEventType());
            }
        } catch (Exception e) { log.error("处理订单事件失败", e); }
    }

    @KafkaListener(topics = "user-behavior", groupId = "shopping-behavior-group")
    public void handleUserBehaviorEvent(ConsumerRecord<String, String> record) {
        try {
            UserBehaviorEvent event = objectMapper.readValue(record.value(), UserBehaviorEvent.class);
            log.info("收到用户行为事件: type={}, userId={}, productId={}", event.getEventType(), event.getUserId(), event.getProductId());
        } catch (Exception e) { log.error("处理用户行为事件失败", e); }
    }
}
