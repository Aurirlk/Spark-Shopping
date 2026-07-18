package com.shopping.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
public class CreateOrderDTO {

    /** 购物车ID列表（从购物车结算时传） */
    private List<Long> cartIds;

    /** 直接购买商品列表（立即购买时传） */
    private List<DirectItem> items;

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    private String note;

    private Integer payType = 1;

    @Data
    public static class DirectItem {
        private Long productId;
        private Integer quantity;
    }
}
