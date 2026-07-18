package com.shopping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon")
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private BigDecimal conditionAmount; // 满减条件
    private BigDecimal discountAmount;  // 减免金额
    private Integer stock;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status; // 1-有效 0-失效
    private LocalDateTime createTime;
}
