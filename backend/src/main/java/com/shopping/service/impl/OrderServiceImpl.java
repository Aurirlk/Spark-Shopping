package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopping.dto.CreateOrderDTO;
import com.shopping.entity.Cart;
import com.shopping.entity.Order;
import com.shopping.entity.OrderItem;
import com.shopping.entity.Product;
import com.shopping.exception.BusinessException;
import com.shopping.mapper.OrderMapper;
import com.shopping.service.CartService;
import com.shopping.service.OrderItemService;
import com.shopping.service.OrderService;
import com.shopping.service.ProductService;
import com.shopping.utils.UserContext;
import com.shopping.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemService orderItemService;

    @Override
    @Transactional
    public OrderVO createOrder(CreateOrderDTO dto) {
        Long userId = UserContext.getUserId();
        List<Cart> carts = java.util.Collections.emptyList();
        List<CreateOrderDTO.DirectItem> directItems = dto.getItems();

        // 1. 验证商品
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new java.util.ArrayList<>();

        if (directItems != null && !directItems.isEmpty()) {
            // 立即购买
            for (CreateOrderDTO.DirectItem item : directItems) {
                Product p = productService.getById(item.getProductId());
                if (p == null || p.getStatus() != 1)
                    throw new BusinessException("商品已下架");
                if (p.getStock() < item.getQuantity())
                    throw new BusinessException("商品" + p.getName() + "库存不足");

                BigDecimal sub = p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(sub);

                OrderItem oi = new OrderItem();
                oi.setProductId(p.getId());
                oi.setProductName(p.getName());
                oi.setProductImage(p.getMainImage());
                oi.setPrice(p.getPrice());
                oi.setQuantity(item.getQuantity());
                oi.setTotalAmount(sub);
                orderItems.add(oi);

                p.setStock(p.getStock() - item.getQuantity());
                p.setSales(p.getSales() + item.getQuantity());
                productService.updateById(p);
            }
        } else {
            // 购物车结算
            carts = cartService.listByIds(dto.getCartIds());
            if (carts.isEmpty()) throw new BusinessException("购物车为空");
            boolean allBelongToUser = carts.stream()
                    .allMatch(c -> c.getUserId().equals(userId));
            if (!allBelongToUser) throw new BusinessException("购物车商品不属于当前用户");

            for (Cart cart : carts) {
                Product p = productService.getById(cart.getProductId());
                if (p == null || p.getStatus() != 1)
                    throw new BusinessException("商品" + p.getName() + "已下架");
                if (p.getStock() < cart.getQuantity())
                    throw new BusinessException("商品" + p.getName() + "库存不足");

                BigDecimal sub = p.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
                totalAmount = totalAmount.add(sub);

                OrderItem oi = new OrderItem();
                oi.setProductId(p.getId());
                oi.setProductName(p.getName());
                oi.setProductImage(p.getMainImage());
                oi.setPrice(p.getPrice());
                oi.setQuantity(cart.getQuantity());
                oi.setTotalAmount(sub);
                orderItems.add(oi);

                p.setStock(p.getStock() - cart.getQuantity());
                p.setSales(p.getSales() + cart.getQuantity());
                productService.updateById(p);
            }
        }

        // 2. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setPayType(dto.getPayType());
        order.setStatus(0);
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverAddress(dto.getReceiverAddress());
        order.setNote(dto.getNote());
        save(order);

        // 3. 保存订单项
        for (OrderItem oi : orderItems) {
            oi.setOrderId(order.getId());
            orderItemService.save(oi);
        }

        // 4. 清除购物车
        if (dto.getCartIds() != null && !dto.getCartIds().isEmpty())
            cartService.removeByIds(dto.getCartIds());

        // 5. 返回
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        return vo;
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        Order order = getById(orderId);
        if (order == null) throw new BusinessException("订单不存在");

        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setStatus(order.getStatus());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setNote(order.getNote());
        vo.setCreateTime(order.getCreateTime());

        // 设置状态文本
        String[] statusTexts = {"待付款", "待发货", "待收货", "已完成", "已取消"};
        if (order.getStatus() != null && order.getStatus() >= 0 && order.getStatus() < statusTexts.length) {
            vo.setStatusDesc(statusTexts[order.getStatus()]);
        }

        // 查询订单项
        List<OrderItem> items = orderItemService.list(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        vo.setOrderItems(items);
        return vo;
    }

    @Override
    public IPage<OrderVO> getUserOrders(Integer status, Integer pageNum, Integer pageSize) {
        Long userId = UserContext.getUserId();
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) wrapper.eq(Order::getStatus, status);
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> orderPage = baseMapper.selectPage(page, wrapper);
        return orderPage.convert(this::convertToVO);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 0) throw new BusinessException("只能取消待付款订单");
        order.setStatus(4);
        updateById(order);
    }

    @Override
    @Transactional
    public void confirmReceive(Long orderId) {
        Order order = getById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 2) throw new BusinessException("只能确认待收货订单");
        order.setStatus(3);
        order.setReceiveTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() == 1 || order.getStatus() == 2)
            throw new BusinessException("不能删除待发货或待收货订单");
        removeById(orderId);
        orderItemService.remove(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
    }

    private String generateOrderNo() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return ts + String.format("%06d", new Random().nextInt(1000000));
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setUserId(order.getUserId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setStatus(order.getStatus());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setCreateTime(order.getCreateTime());

        String[] statusTexts = {"待付款", "待发货", "待收货", "已完成", "已取消"};
        if (order.getStatus() != null && order.getStatus() >= 0 && order.getStatus() < statusTexts.length)
            vo.setStatusDesc(statusTexts[order.getStatus()]);

        return vo;
    }
}
