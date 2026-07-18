package com.shopping.controller;

import com.shopping.entity.Order;
import com.shopping.service.OrderService;
import com.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/pay/{orderId}")
    public Result<Map<String, Object>> pay(@PathVariable Long orderId,
                                           @RequestParam(defaultValue = "1") Integer payType) {
        Order order = orderService.getById(orderId);
        if (order == null) return Result.error("订单不存在");
        if (order.getStatus() != 0) return Result.error("订单状态异常，不能支付");

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        order.setPayType(payType);
        orderService.updateById(order);

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("status", 1);
        data.put("payTime", LocalDateTime.now().toString());
        data.put("message", "支付成功");
        return Result.success(data);
    }

    @GetMapping("/status/{orderId}")
    public Result<Map<String, Object>> status(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        if (order == null) return Result.error("订单不存在");
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("status", order.getStatus());
        data.put("payTime", order.getPayTime());
        return Result.success(data);
    }
}
