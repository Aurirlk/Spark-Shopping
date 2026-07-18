package com.shopping.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.entity.Order;
import com.shopping.entity.Product;
import com.shopping.exception.BusinessException;
import com.shopping.service.OrderService;
import com.shopping.service.ProductService;
import com.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理后台控制器（B端）
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // ==================== 商品管理 ====================

    /**
     * 分页查询商品（管理端）
     */
    @GetMapping("/product/list")
    public Result<IPage<Product>> getProductList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (name != null && !name.isEmpty()) {
            wrapper.like(Product::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.orderByDesc(Product::getCreateTime);

        IPage<Product> productPage = productService.page(page, wrapper);
        return Result.success(productPage);
    }

    /**
     * 添加商品
     */
    @PostMapping("/product/save")
    public Result<Void> saveProduct(@RequestBody Product product) {
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        product.setSales(0);
        productService.save(product);
        return Result.success("添加成功");
    }

    /**
     * 更新商品
     */
    @PutMapping("/product/update")
    public Result<Void> updateProduct(@RequestBody Product product) {
        Product exist = productService.getById(product.getId());
        if (exist == null) {
            throw new BusinessException("商品不存在");
        }
        product.setUpdateTime(LocalDateTime.now());
        productService.updateById(product);
        return Result.success("更新成功");
    }

    /**
     * 切换商品上下架状态
     */
    @PutMapping("/product/status/{id}")
    public Result<Void> toggleProductStatus(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(product.getStatus() == 1 ? 0 : 1);
        product.setUpdateTime(LocalDateTime.now());
        productService.updateById(product);
        return Result.success(product.getStatus() == 1 ? "上架成功" : "下架成功");
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/product/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        productService.removeById(id);
        return Result.success("删除成功");
    }

    // ==================== 订单管理 ====================

    /**
     * 分页查询订单（管理端）
     */
    @GetMapping("/order/list")
    public Result<IPage<Order>> getOrderList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        IPage<Order> orderPage = orderService.page(page, wrapper);
        return Result.success(orderPage);
    }

    /**
     * 发货
     */
    @PutMapping("/order/ship/{orderId}")
    public Result<Void> shipOrder(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("只能对待发货订单进行发货");
        }
        order.setStatus(2);
        order.setSendTime(LocalDateTime.now());
        orderService.updateById(order);
        return Result.success("发货成功");
    }

    /**
     * 完成订单
     */
    @PutMapping("/order/complete/{orderId}")
    public Result<Void> completeOrder(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        order.setStatus(3);
        order.setReceiveTime(LocalDateTime.now());
        orderService.updateById(order);
        return Result.success("订单已完成");
    }

    /**
     * 取消订单（管理端）
     */
    @PutMapping("/order/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        order.setStatus(4);
        orderService.updateById(order);
        return Result.success("取消成功");
    }
}
