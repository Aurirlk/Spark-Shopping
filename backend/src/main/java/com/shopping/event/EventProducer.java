package com.shopping.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendOrderEvent(OrderEvent event) {
        try { kafkaTemplate.send("order-events", event.getOrderNo(), objectMapper.writeValueAsString(event)); log.info("发送订单事件: {}", event.getEventType()); }
        catch (Exception e) { log.error("发送订单事件失败", e); }
    }
    public void sendUserBehaviorEvent(UserBehaviorEvent event) {
        try { kafkaTemplate.send("user-behavior", String.valueOf(event.getUserId()), objectMapper.writeValueAsString(event)); log.info("发送用户行为事件: {}", event.getEventType()); }
        catch (Exception e) { log.error("发送用户行为事件失败", e); }
    }
}
