package com.shopping.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shopping.dto.CreateOrderDTO;
import com.shopping.entity.Order;
import com.shopping.vo.OrderVO;

public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    OrderVO createOrder(CreateOrderDTO dto);

    /**
     * 获取订单详情
     */
    OrderVO getOrderDetail(Long orderId);

    /**
     * 分页查询用户订单
     */
    IPage<OrderVO> getUserOrders(Integer status, Integer pageNum, Integer pageSize);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId);

    /**
     * 确认收货
     */
    void confirmReceive(Long orderId);

    /**
     * 删除订单
     */
    void deleteOrder(Long orderId);
}
