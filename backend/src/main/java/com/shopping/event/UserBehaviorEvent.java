package com.shopping.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserBehaviorEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String eventType; // PRODUCT_VIEW, FAVORITE, CART, PURCHASE
    private Long userId;
    private Long productId;
    private Long categoryId;
    private String deviceType;
    private Long timestamp;

    public static UserBehaviorEvent productView(Long userId, Long productId, Long categoryId) {
        return UserBehaviorEvent.builder().eventType("PRODUCT_VIEW").userId(userId).productId(productId).categoryId(categoryId).timestamp(System.currentTimeMillis()).build();
    }
}
