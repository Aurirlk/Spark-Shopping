package com.shopping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopping.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
