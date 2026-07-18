package com.shopping.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String eventType; // ORDER_CREATED, PAID, SHIPPED, COMPLETED, CANCELLED
    private Long orderId;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private Long timestamp;

    public static OrderEvent created(Long orderId, String orderNo, Long userId, BigDecimal totalAmount) {
        return OrderEvent.builder().eventType("ORDER_CREATED").orderId(orderId).orderNo(orderNo).userId(userId).totalAmount(totalAmount).timestamp(System.currentTimeMillis()).build();
    }
    public static OrderEvent paid(Long orderId, String orderNo, Long userId) {
        return OrderEvent.builder().eventType("ORDER_PAID").orderId(orderId).orderNo(orderNo).userId(userId).timestamp(System.currentTimeMillis()).build();
    }
    public static OrderEvent cancelled(Long orderId, String orderNo, Long userId) {
        return OrderEvent.builder().eventType("ORDER_CANCELLED").orderId(orderId).orderNo(orderNo).userId(userId).timestamp(System.currentTimeMillis()).build();
    }
}
