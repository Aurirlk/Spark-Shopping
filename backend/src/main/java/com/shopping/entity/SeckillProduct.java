package com.shopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @TableName("seckill_product")
public class SeckillProduct {
    @TableId(type = IdType.AUTO) private Long id;
    private Long productId;
    private BigDecimal seckillPrice;
    private Integer stock;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private LocalDateTime createTime;
}
