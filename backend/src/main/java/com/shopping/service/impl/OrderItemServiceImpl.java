package com.shopping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopping.entity.OrderItem;
import com.shopping.mapper.OrderItemMapper;
import com.shopping.service.OrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
}
