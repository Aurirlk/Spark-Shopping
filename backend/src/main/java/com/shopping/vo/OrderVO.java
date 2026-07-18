package com.shopping.vo;

import com.shopping.entity.Order;
import com.shopping.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {

    private Long id;

    private Long userId;

    private String orderNo;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private BigDecimal freightAmount;

    private Integer status;

    private String statusDesc;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String note;

    private LocalDateTime createTime;

    private List<OrderItem> orderItems;

    public static OrderVO fromOrder(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setUserId(order.getUserId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setFreightAmount(order.getFreightAmount());
        vo.setStatus(order.getStatus());
        vo.setStatusDesc(getStatusDesc(order.getStatus()));
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setNote(order.getNote());
        vo.setCreateTime(order.getCreateTime());
        return vo;
    }

    private static String getStatusDesc(Integer status) {
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "待收货";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知状态";
        }
    }
}
