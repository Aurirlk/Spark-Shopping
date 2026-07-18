package com.shopping.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopping.dto.CreateOrderDTO;
import com.shopping.service.OrderService;
import com.shopping.utils.Result;
import com.shopping.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<OrderVO> createOrder(@RequestBody @Validated CreateOrderDTO dto) {
        OrderVO orderVO = orderService.createOrder(dto);
        return Result.success(orderVO);
    }

    @GetMapping("/detail/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        OrderVO orderVO = orderService.getOrderDetail(orderId);
        return Result.success(orderVO);
    }

    @GetMapping("/list")
    public Result<IPage<OrderVO>> getUserOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        IPage<OrderVO> orderPage = orderService.getUserOrders(status, pageNum, pageSize);
        return Result.success(orderPage);
    }

    @PutMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return Result.success("取消成功");
    }

    @PutMapping("/confirm/{orderId}")
    public Result<Void> confirmReceive(@PathVariable Long orderId) {
        orderService.confirmReceive(orderId);
        return Result.success("确认收货成功");
    }

    @DeleteMapping("/delete/{orderId}")
    public Result<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return Result.success("删除成功");
    }
}
