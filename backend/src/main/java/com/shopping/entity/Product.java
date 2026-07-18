package com.shopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long categoryId;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private Integer sales;

    private String mainImage;

    private String subImages;

    private String description;

    private String detail;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;
}
