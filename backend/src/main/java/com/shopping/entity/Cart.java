package com.shopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cart")
public class Cart {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long productId;

    private Integer quantity;

    private Integer checked;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 商品信息（非数据库字段）
     */
    @TableField(exist = false)
    private Product product;

    /**
     * 小计金额（非数据库字段）
     */
    @TableField(exist = false)
    private BigDecimal subtotal;
}
