package com.shopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("`order`")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private BigDecimal freightAmount;

    private Integer payType;

    /**
     * 订单状态：
     * 0-待付款
     * 1-待发货
     * 2-待收货
     * 3-已完成
     * 4-已取消
     */
    private Integer status;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String note;

    private LocalDateTime payTime;

    private LocalDateTime sendTime;

    private LocalDateTime receiveTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 订单项列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<OrderItem> orderItems;
}
